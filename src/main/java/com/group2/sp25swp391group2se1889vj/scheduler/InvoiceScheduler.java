package com.group2.sp25swp391group2se1889vj.scheduler;

import com.group2.sp25swp391group2se1889vj.controller.WebSocketController;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import com.group2.sp25swp391group2se1889vj.entity.Invoice;
import com.group2.sp25swp391group2se1889vj.entity.InvoiceItem;
import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import com.group2.sp25swp391group2se1889vj.repository.CustomerRepository;
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
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceScheduler {
    private final InvoiceRepository invoiceRepository;
    private final DebtRepository debtRepository;
    private final ProductRepository productRepository;
    private final WebSocketController webSocketController;

    // chạy sau mối 5 giây
    @Transactional
    @Scheduled(fixedRate = 10000)
    public void processInvoice() {
        log.info("Invoice processed");
        List<Invoice> pendingInvoices = invoiceRepository.findInvoiceByIsProcessedIsFalse();
        for (Invoice invoice : pendingInvoices) {
            try {
                if (invoice.getType() == InvoiceType.PURCHASE) {
                    log.info("Đang xử lý hóa đơn nhập hàng: {}", invoice.getId());
                    processInvoicePurchase(invoice);
                    invoice.setProcessed(true);
                    invoiceRepository.save(invoice);
                    webSocketController.sendInvoiceStatus(invoice.getId(), "SUCCESS", "Hóa đơn nhập hàng đã xử lý", invoice.getCreatedBy().getUsername());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("Error processing invoice: {}", invoice.getId());
            }
        }

    }

    @Transactional
    protected void processInvoicePurchase(Invoice invoice) {
        // 1. Tính tổng tiền hàng nhập từ khách hàng
        BigDecimal totalPrice = invoice.getInvoiceItems().stream()
                .map(InvoiceItem::getPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalPrice(totalPrice);

        // 2. Lấy thông tin khách hàng và số dư hiện tại
        Customer customer = invoice.getCustomer();
        BigDecimal customerBalance = customer.getBalance();
        invoice.setCustomerBalance(customerBalance);

        // 3. Tổng tiền phải trả cho khách hàng (bao gồm số dư trước đó)
        BigDecimal totalPayable = totalPrice.add(customerBalance);
        invoice.setTotalPayable(totalPayable);

        // 4. Số tiền đã trả thực tế
        BigDecimal totalPaid = invoice.getTotalPaid();

        // 5. Tính tổng nợ còn lại sau khi trả
        BigDecimal totalDebt = totalPayable.subtract(totalPaid);
        invoice.setTotalDebt(totalDebt);

        // 6. Xử lý công nợ theo từng case
        if (customerBalance.compareTo(BigDecimal.ZERO) == 0) {
            // 🟢 CASE: SỐ DƯ KHÁCH HÀNG = 0
            if (totalPaid.compareTo(totalPrice) < 0) {
                // Trả không đủ -> Cửa hàng nợ khách (SHOP_BORROW)
                createDebt(invoice, DebtType.SHOP_BORROW, totalPrice.subtract(totalPaid), "Cửa hàng nợ khách sau nhập hàng");
            } else if (totalPaid.compareTo(totalPrice) > 0) {
                // Trả dư -> Khách nợ cửa hàng (CUSTOMER_BORROW)
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPaid.subtract(totalPrice), "Khách nợ cửa hàng sau nhập hàng");
            }
        } else if (customerBalance.compareTo(BigDecimal.ZERO) > 0) {
            // 🟢 CASE: SỐ DƯ KHÁCH HÀNG > 0
            if (totalPaid.compareTo(totalPrice) == 0) {
                // Trả đúng số tiền hàng -> Không tạo công nợ
                return;
            } else if (totalPaid.compareTo(totalPrice.add(customerBalance)) == 0) {
                // Trả đúng cả tiền hàng và số dư -> Cửa hàng trả nợ khách (SHOP_REPAY)
                createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách");
            } else if (totalPaid.compareTo(totalPrice) > 0 && totalPaid.compareTo(totalPrice.add(customerBalance)) < 0) {
                // Trả dư nhưng chưa hết số dư -> Cửa hàng trả nợ khách một phần
                createDebt(invoice, DebtType.SHOP_REPAY, totalPaid.subtract(totalPrice), "Cửa hàng trả một phần nợ khách");
            } else if (totalPaid.compareTo(totalPrice.add(customerBalance)) > 0) {
                // Trả dư quá mức -> Trả hết nợ khách và khách lại nợ cửa hàng
                createDebt(invoice, DebtType.SHOP_REPAY, customerBalance, "Cửa hàng trả nợ khách");
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPaid.subtract(totalPrice).subtract(customerBalance), "Khách nợ cửa hàng sau trả dư");
            } else if (totalPaid.compareTo(totalPrice) < 0) {
                // Trả thiếu -> Cửa hàng tiếp tục nợ khách
                createDebt(invoice, DebtType.SHOP_BORROW, totalPrice.subtract(totalPaid), "Cửa hàng tiếp tục nợ khách");
            }
        } else {
            // 🟢 CASE: SỐ DƯ KHÁCH HÀNG < 0
            if (totalPaid.compareTo(totalPrice) == 0) {
                // Trả đúng số tiền hàng -> Không tạo công nợ
                return;
            } else if (totalPaid.compareTo(totalPrice.add(customerBalance)) == 0) {
                // Trả đủ cả tiền hàng và số dư âm -> Khách trả nợ cửa hàng
                createDebt(invoice, DebtType.CUSTOMER_REPAY, customerBalance.abs(), "Khách trả nợ cửa hàng");
            } else if (totalPaid.compareTo(totalPrice) > 0 && totalPaid.compareTo(totalPrice.add(customerBalance)) < 0) {
                // Trả dư nhưng chưa đủ để xóa nợ -> Khách trả nợ một phần
                createDebt(invoice, DebtType.CUSTOMER_REPAY, totalPaid.subtract(totalPrice), "Khách trả một phần nợ cửa hàng");
            } else if (totalPaid.compareTo(totalPrice.add(customerBalance)) > 0) {
                // Trả dư quá mức -> Xóa nợ khách và cửa hàng lại nợ khách
                createDebt(invoice, DebtType.CUSTOMER_REPAY, customerBalance.abs(), "Khách trả hết nợ cửa hàng");
                createDebt(invoice, DebtType.SHOP_REPAY, totalPaid.subtract(totalPrice).subtract(customerBalance.abs()), "Cửa hàng trả nợ khách sau thanh toán");
            } else if (totalPaid.compareTo(totalPrice) < 0) {
                // Trả không đủ -> Khách tiếp tục nợ cửa hàng
                createDebt(invoice, DebtType.CUSTOMER_BORROW, totalPrice.subtract(totalPaid), "Khách tiếp tục nợ cửa hàng");
            }
        }

        // cập nhật số lượng sản phẩm
        for (InvoiceItem item : invoice.getInvoiceItems()) {
            log.info("Cập nhật số lượng sản phẩm: {} - {}", item.getProduct().getName(), item.getQuantity());
            productRepository.increaseStockQuantity(item.getProduct().getId(), item.getQuantity() * item.getProductPackage().getWeight());
        }
    }

    /**
     * Phương thức tạo bản ghi nợ
     */
    private void createDebt(Invoice invoice, DebtType type, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return; // Không tạo nếu số tiền không hợp lệ

        Debt debt = new Debt();
        debt.setType(type);
        debt.setCustomer(invoice.getCustomer());
        debt.setAmount(amount);
        debt.setDebtAt(LocalDateTime.now());
        debt.setDescription(description + " #" + invoice.getId());
        debt.setProcessed(false);
        debt.setSuccess(false);
        debtRepository.save(debt);
    }


}
