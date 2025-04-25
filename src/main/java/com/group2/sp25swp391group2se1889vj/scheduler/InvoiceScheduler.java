package com.group2.sp25swp391group2se1889vj.scheduler;

import com.group2.sp25swp391group2se1889vj.controller.InvoiceRestController;
import com.group2.sp25swp391group2se1889vj.controller.WebSocketController;
import com.group2.sp25swp391group2se1889vj.entity.*;
import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import com.group2.sp25swp391group2se1889vj.repository.DebtRepository;
import com.group2.sp25swp391group2se1889vj.repository.InvoiceRepository;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceScheduler {
    private final InvoiceRepository invoiceRepository;
    private final DebtRepository debtRepository;
    private final ProductRepository productRepository;
    private final WebSocketController webSocketController;

    public static boolean isProcessing = false;


    @Transactional
    public void processInvoice() {
        Long invoiceId;
        if (isProcessing) {
            return;
        }
        while ((invoiceId = InvoiceRestController.invoiceQueue.poll()) != null) {
            isProcessing = true;
            var invoice = invoiceRepository.findById(invoiceId).orElse(null);
            try {
                assert invoice != null;
                if (invoice.getType() == InvoiceType.PURCHASE) {
                    processInvoicePurchase(invoice);
                    invoice.setProcessed(true);
                    invoice.setSuccess(Boolean.TRUE);
                    invoiceRepository.save(invoice);
                    webSocketController.sendInvoiceStatus(invoice.getId(), "SUCCESS", "Hóa đơn nhập hàng đã xử lý", invoice.getCreatedBy().getUsername());
                } else if (invoice.getType() == InvoiceType.SALES) {
                    processInvoiceSale(invoice);
                    invoice.setProcessed(true);
                    invoice.setSuccess(Boolean.TRUE);
                    invoiceRepository.save(invoice);
                    webSocketController.sendInvoiceStatus(invoice.getId(), "SUCCESS", "Hóa đơn bán hàng đã xử lý", invoice.getCreatedBy().getUsername());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Error processing invoice: {}", invoice.getId());
                invoice.setSuccess(Boolean.FALSE);
                invoice.setProcessed(Boolean.TRUE);
                webSocketController.sendInvoiceStatus(invoice.getId(), "ERROR", e.getMessage(), invoice.getCreatedBy().getUsername());
            }
        }
        isProcessing = false;


    }

    @Transactional
    protected void processInvoicePurchase(Invoice invoice) {
        BigDecimal totalPrice = invoice.getInvoiceItems().stream()
                .map(InvoiceItem::getPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalPrice(totalPrice);

        invoice.setTotalDiscount(totalPrice);

        Customer customer = invoice.getCustomer();
        BigDecimal customerBalance = customer.getBalance();
        invoice.setCustomerBalance(customerBalance);

        BigDecimal totalPayable = totalPrice.add(customerBalance);
        invoice.setTotalPayable(totalPayable);

        invoice.setTotalDiscount(totalPrice);

        BigDecimal totalPaid = invoice.getTotalPaid();

        BigDecimal totalDebt = totalPayable.subtract(totalPaid);
        invoice.setTotalDebt(totalDebt);


        for (InvoiceItem item : invoice.getInvoiceItems()) {
            productRepository.increaseStockQuantity(item.getProduct().getId(), item.getQuantity() * item.getProductPackage().getWeight());
        }

        if (customerBalance.compareTo(BigDecimal.ZERO) == 0) {
            // log.info("Trường hợp số dư khách hàng = 0");
            if (totalPaid.compareTo(totalPrice) < 0) {
                // log.info("Trường hợp cửa hàng không trả đủ tiền hàng -> Cửa hàng nợ khách (SHOP_BORROW)");
                createDebt(invoice, DebtType.SHOP_BORROW, totalPrice.subtract(totalPaid), "Cửa hàng nợ khách sau nhập hàng");
            } else if (totalPaid.compareTo(totalPrice) > 0) {
                // log.info("Trường hợp cửa hàng trả dư tiền hàng -> Khách nợ cửa hàng (CUSTOMER_BORROW)");
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPaid.subtract(totalPrice), "Khách nợ cửa hàng sau nhập hàng");
            }
        } else if (customerBalance.compareTo(BigDecimal.ZERO) > 0) {
            // log.info("Trường hợp số dư khách hàng > 0 (cửa hàng nợ khách)");
            if (totalPaid.compareTo(totalPrice) == 0) {
                // log.info("Trả đúng số tiền hàng -> Không tạo công nợ");
                return;
            }
            if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
                // log.info("Trường hợp cửa hàng không trả tiền cho khách -> Cửa hàng nợ khách toàn bộ tiền hàng");
                createDebt(invoice, DebtType.SHOP_BORROW, totalPrice, "Cửa hàng nợ khách sau nhập hàng");
            }
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                // log.info("Trường hợp cửa hàng trả tiền cho khách");
                if (totalPaid.compareTo(totalPrice) < 0) {
                    // log.info("Trả thiếu tiền hàng -> Cửa hàng nợ khách một phần");
                    createDebt(invoice, DebtType.SHOP_BORROW, totalPrice.subtract(totalPaid), "Cửa hàng nợ khách sau nhập hàng");
                }
                if (totalPaid.compareTo(totalPrice) == 0) {
                    // log.info("Trả đúng tiền hàng -> Không tạo công nợ");
                    return;
                }
                if (totalPaid.compareTo(totalPrice) > 0 && totalPaid.subtract(totalPrice).compareTo(customerBalance) <= 0) {
                    // log.info("Trả dư tiền hàng và nhỏ hơn hoặc bằng số dư -> Cửa hàng trả nợ khách");
                    createDebt(invoice, DebtType.SHOP_REPAY, totalPaid.subtract(totalPrice), "Cửa hàng trả nợ khách sau nhập hàng");
                }
                if (totalPaid.compareTo(totalPrice) > 0 && totalPaid.subtract(totalPrice).compareTo(customerBalance) > 0) {
                    // log.info("Trả dư tiền hàng và lớn hơn số dư -> Cửa hàng trả nợ khách toàn bộ và khách nợ cửa hàng");
                    createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách sau nhập hàng");
                    createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPaid.subtract(totalPrice).subtract(customerBalance), "Khách nợ cửa hàng sau nhập hàng");
                }
            }
            if (totalPaid.compareTo(BigDecimal.ZERO) < 0) {
                // log.info("Trường hợp khách trả thêm tiền cho cửa hàng -> Cửa hàng nợ tiền hàng + số tiền trả thêm");
                createDebt(invoice, DebtType.SHOP_BORROW, totalPrice.add(totalPaid.abs()), "Cửa hàng nợ khách sau nhập hàng");
            }
        } else {
            // log.info("Trường hợp số dư khách hàng < 0 (Khách hàng nợ cửa hàng)");
            if (totalPaid.compareTo(totalPrice) == 0) {
                // log.info("Trả đúng số tiền hàng -> Không tạo công nợ");
                return;
            }
            if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
                // log.info("Trường hợp cửa hàng không trả tiền cho khách");
                if (totalPrice.compareTo(customerBalance.abs()) <= 0) {
                    // log.info("Tổng tiền hàng nhỏ hơn hoặc bằng số dư -> Khách trả nợ cửa hàng");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, totalPrice, "Khách trả nợ cửa hàng sau nhập hàng");
                } else {
                    // log.info("Tổng tiền hàng lớn hơn số dư -> Khách trả nợ cửa hàng toàn bộ và cửa hàng nợ khách");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, customerBalance.abs(), "Khách trả nợ cửa hàng sau nhập hàng");
                    createDebt(invoice, DebtType.SHOP_BORROW, totalPrice.subtract(customerBalance.abs()), "Cửa hàng nợ khách sau nhập hàng");
                }
            }
            if (totalPaid.compareTo(BigDecimal.ZERO) < 0) {
                // log.info("Trường hợp khách trả thêm tiền cho cửa hàng");
                // nếu số tiền trả thêm + số tiền hàng <= số nợ thì khách trả nợ
                if (totalPaid.abs().add(totalPrice).compareTo(customerBalance.abs()) <= 0) {
                    // log.info("Số tiền trả thêm + số tiền hàng <= số nợ -> Khách trả nợ cửa hàng");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, totalPaid.abs().add(totalPrice), "Khách trả nợ cửa hàng sau nhập hàng");
                } else {
                    // log.info("Số tiền trả thêm + số tiền hàng > số nợ -> Khách trả nợ toàn bộ và cửa hàng nợ khách");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, customerBalance.abs(), "Khách trả nợ cửa hàng sau nhập hàng");
                    createDebt(invoice, DebtType.SHOP_BORROW, totalPaid.abs().add(totalPrice).subtract(customerBalance.abs()), "Cửa hàng nợ khách sau nhập hàng");
                }
            }
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                // log.info("Trường hợp cửa hàng trả thêm tiền cho khách");
                // nếu số tiền trả thêm + số tiền hàng <= số nợ thì khách trả nợ
                if (totalPaid.add(totalPrice).compareTo(customerBalance.abs()) <= 0) {
                    // log.info("Số tiền trả thêm + số tiền hàng <= số nợ -> khách trả nợ cửa hàng");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, totalPaid.add(totalPrice), "Khách trả nợ cửa hàng sau nhập hàng");
                } else {
                    // log.info("Số tiền trả thêm + số tiền hàng > số nợ -> khách trả nợ toàn bộ và cửa hàng nợ khách");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, customerBalance.abs(), "Khách trả nợ cửa hàng sau nhập hàng");
                    createDebt(invoice, DebtType.SHOP_BORROW, totalPaid.add(totalPrice).subtract(customerBalance.abs()), "Cửa hàng nợ khách sau nhập hàng");
                }
            }
        }
    }

    @Transactional
    protected void processInvoiceSale(Invoice invoice) {
        BigDecimal totalPrice = invoice.getInvoiceItems().stream()
                .map(item -> {
                    ProductPackage productPackage = item.getProductPackage();
                    BigDecimal price = item.getProduct().getPrice();
                    int weight = productPackage.getWeight();
                    return price.multiply(BigDecimal.valueOf(weight)).multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalPrice(totalPrice);

        BigDecimal totalDiscount = invoice.getInvoiceItems().stream()
                .map(InvoiceItem::getPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalDiscount(totalDiscount);

        Customer customer = invoice.getCustomer();
        BigDecimal customerBalance = customer.getBalance();
        invoice.setCustomerBalance(customerBalance);

        BigDecimal totalPayable = totalDiscount.subtract(customerBalance);
        invoice.setTotalPayable(totalPayable);

        BigDecimal totalPaid = invoice.getTotalPaid();

        BigDecimal totalDebt = totalPayable.subtract(totalPaid);
        invoice.setTotalDebt(totalDebt);

        for (InvoiceItem item : invoice.getInvoiceItems()) {
            int row = productRepository.decreaseStockQuantity(item.getProduct().getId(), item.getQuantity() * item.getProductPackage().getWeight());
            if (row == 0) {
                throw new RuntimeException("Sản phẩm  không đủ hàng trong kho");
            }
        }
        if (customerBalance.compareTo(BigDecimal.ZERO) == 0) {
            // log.info("Số dư khách hàng = 0");
            if (totalPaid.compareTo(totalDiscount) < 0) {
                // log.info("Trả không đủ tiền hàng -> Khách nợ cửa hàng (CUSTOMER_BORROW)");
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalDiscount.subtract(totalPaid), "Khách nợ cửa hàng sau mua hàng");

            } else if (totalPaid.compareTo(totalDiscount) > 0) {
                // log.info("Trả dư tiền hàng -> Cửa hàng nợ khách (SHOP_BORROW)");
                createDebt(invoice, DebtType.SHOP_BORROW, totalPaid.subtract(totalDiscount), "Cửa hàng nợ khách sau mua hàng");
            }
        } else if (customerBalance.compareTo(BigDecimal.ZERO) > 0) {
            // log.info("SỐ DƯ KHÁCH HÀNG > 0 (Cửa hàng đang nợ khách)");
            if (totalPaid.compareTo(totalDiscount) == 0) {
                // log.info("Khách trả đúng số tiền hàng -> Không tạo công nợ");
            } else if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
                // log.info("Khách không trả tiền hàng");
                // nếu số dư khách hàng lớn hơn hoặc bằng tổng tiền hàng thì cửa hàng trả nợ khách
                if (customerBalance.compareTo(totalDiscount) >= 0) {
                    // log.info("Số dư khách hàng lớn hơn hoặc bằng tổng tiền hàng -> Cửa hàng trả nợ khách (SHOP_REPAY)");
                    createDebt(invoice, DebtType.SHOP_REPAY, totalDiscount, "Cửa hàng trả nợ khách sau mua hàng");
                } else {
                    // log.info("Số dư khách hàng nhỏ hơn tổng tiền hàng -> cửa hàng trả nợ toàn bộ và khách nợ cửa hàng");
                    createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách sau mua hàng");
                    createDebt(invoice, DebtType.CUSTOMER_BORROW, totalDiscount.subtract(customerBalance), "Khách nợ cửa hàng sau mua hàng");
                }

            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                // log.info("trường hợp khách trả thêm tiền cho cửa hàng");
                if (totalPaid.compareTo(totalDiscount) < 0 && totalPaid.add(totalDiscount).subtract(customerBalance).compareTo(BigDecimal.ZERO) < 0) {
                    // log.info("trả thiếu tiền hàng nhưng vẫn còn số dư -> Cửa hàng trả nợ khách (SHOP_REPAY)");
                    createDebt(invoice, DebtType.SHOP_REPAY, totalDiscount.subtract(totalPaid), "Cửa hàng trả nợ khách sau mua hàng");
                } else if (totalPaid.compareTo(totalDiscount) < 0 && totalPaid.add(totalDiscount).subtract(customerBalance).compareTo(BigDecimal.ZERO) == 0) {
                    // log.info("trả thiếu tiền hàng và số dư đủ -> Cửa hàng trả nợ khách toàn bộ (SHOP_REPAY)");
                    createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách sau mua hàng");
                } else if (totalPaid.compareTo(totalDiscount) < 0 && totalPaid.add(totalDiscount).subtract(customerBalance).compareTo(BigDecimal.ZERO) > 0) {
                    // log.info("trả thiếu tiền hàng và số dư không đủ -> Cửa hàng trả nợ khách toàn bộ và khách nợ cửa hàng");
                    createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách sau mua hàng");
                    createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPaid.add(totalDiscount).subtract(customerBalance), "Khách nợ cửa hàng sau mua hàng");
                } else if (totalPaid.compareTo(totalDiscount) > 0) {
                    // log.info("trả dư tiền hàng -> Cửa hàng nợ khách tiếp");
                    createDebt(invoice, DebtType.SHOP_BORROW, totalPaid.subtract(totalDiscount), "Cửa hàng nợ khách sau mua hàng");
                }
            } else if (totalPaid.compareTo(BigDecimal.ZERO) < 0) {
                // log.info("trường hợp cửa hàng trả thêm tiền cho khách");
                if (totalPaid.abs().add(totalDiscount).subtract(customerBalance).compareTo(BigDecimal.ZERO) < 0) {
                    // log.info("tổng tiền hàng và tiền trả thêm không đủ số dư -> cửa hàng trả nợ khách một phần");
                    createDebt(invoice, DebtType.SHOP_REPAY, totalPaid.abs().add(totalDiscount), "Cửa hàng trả nợ khách sau mua hàng");
                } else if (totalPaid.abs().add(totalDiscount).subtract(customerBalance).compareTo(BigDecimal.ZERO) == 0) {
                    // log.info("tổng tiền hàng và tiền trả thêm bằng số dư -> cửa hàng trả nợ khách");
                    createDebt(invoice, DebtType.SHOP_REPAY, totalPaid.abs().add(totalDiscount), "Cửa hàng trả nợ khách sau mua hàng");
                } else if (totalPaid.abs().add(totalDiscount).subtract(customerBalance).compareTo(BigDecimal.ZERO) > 0) {
                    // log.info("tổng tiền hàng và tiền trả thêm lớn hơn số dư -> cửa hàng trả nợ khách toàn bộ và khách nợ cửa hàng");
                    createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách sau mua hàng");
                    createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPaid.abs().add(totalDiscount).subtract(customerBalance), "Khách nợ cửa hàng sau mua hàng");
                }
            }
        } else if (customerBalance.compareTo(BigDecimal.ZERO) < 0) {
            // log.info("SỐ DƯ KHÁCH HÀNG < 0 (Khách hàng nợ cửa hàng)");
            if (totalPaid.compareTo(totalDiscount) == 0) {
                // log.info("Khách trả đúng số tiền hàng -> Không tạo công nợ");
            } else if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
                // log.info("Khách không trả tiền hàng -> khách nợ cửa hàng toàn bộ tiền hàng");
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalDiscount, "Khách nợ cửa hàng sau mua hàng");
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                // log.info("trường hợp khách trả thêm tiền cho cửa hàng");
                if (totalPaid.compareTo(totalDiscount) < 0) {
                    // log.info("trả thiếu tiền hàng -> khách nợ cửa hàng");
                    createDebt(invoice, DebtType.CUSTOMER_BORROW, totalDiscount.subtract(totalPaid), "Khách nợ cửa hàng sau mua hàng");
                } else if (totalPaid.compareTo(totalDiscount) > 0 && totalPaid.subtract(totalDiscount).compareTo(customerBalance.abs()) < 0) {
                    // log.info("trả dư tiền hàng nhưng chưa đủ trả nợ -> khách trả nợ một phần");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, totalPaid.subtract(totalDiscount), "Khách trả nợ cửa hàng sau mua hàng");
                } else if (totalPaid.compareTo(totalDiscount) > 0 && totalPaid.subtract(totalDiscount).compareTo(customerBalance.abs()) == 0) {
                    // log.info("trả nhiều hơn tiền hàng và đủ trả nợ-> khách trả nợ toàn bộ");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, totalPaid.subtract(totalDiscount), "Khách trả nợ cửa hàng sau mua hàng");
                } else if (totalPaid.compareTo(totalDiscount) > 0 && totalPaid.subtract(totalDiscount).compareTo(customerBalance.abs()) > 0) {
                    // log.info("trả nhiều hơn tiền hàng và số dư quá mức -> khách trả nợ toàn bộ và cửa hàng nợ khách");
                    createDebt(invoice, DebtType.CUSTOMER_REPAY, customerBalance.abs(), "Khách trả nợ cửa hàng sau mua hàng");
                    createDebt(invoice, DebtType.SHOP_BORROW, totalPaid.subtract(totalDiscount).subtract(customerBalance.abs()), "Cửa hàng nợ khách sau mua hàng");
                }
            } else if (totalPaid.compareTo(BigDecimal.ZERO) < 0) {
                // log.info("trường hợp cửa hàng trả thêm tiền cho khách");
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalDiscount.add(totalPaid.abs()), "Khách nợ cửa hàng sau mua hàng");
            }
        }


    }


    @Transactional
    protected void createDebt(Invoice invoice, DebtType type, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return; // Không tạo nếu số tiền không hợp lệ

        Debt debt = new Debt();
        debt.setType(type);
        debt.setCustomer(invoice.getCustomer());
        debt.setAmount(amount);
        debt.setDebtAt(LocalDateTime.now());
        debt.setDescription(description + " # <a class=\"link\" href=\"/invoice/detail/" + invoice.getId() + "\">Xem chi tiết hóa đơn</a>");
        debt.setProcessed(false);
        debt.setSuccess(false);
        debtRepository.save(debt);
    }

}
