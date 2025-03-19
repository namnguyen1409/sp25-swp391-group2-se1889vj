package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceDTO;
import com.group2.sp25swp391group2se1889vj.dto.InvoiceFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductSnapshotDTO;
import com.group2.sp25swp391group2se1889vj.dto.InvoiceDetailDTO;
import com.group2.sp25swp391group2se1889vj.entity.*;
import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import com.group2.sp25swp391group2se1889vj.mapper.ProductMapper;
import com.group2.sp25swp391group2se1889vj.repository.*;
import com.group2.sp25swp391group2se1889vj.service.InvoiceService;
import com.group2.sp25swp391group2se1889vj.specification.InvoiceSpecification;
import com.group2.sp25swp391group2se1889vj.util.XSSProtectedUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final DebtRepository debtRepository;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final XSSProtectedUtil xssProtectedUtil;
    private final ProductPackageRepository productPackageRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional
    @Override
    public Long createInvoice(InvoiceDTO invoiceDTO) {
        if (Objects.equals(invoiceDTO.getType(), "PURCHASE")) {
            Invoice invoice = new Invoice();
            invoice.setType(InvoiceType.PURCHASE); // loại hóa đơn
            invoice.setTotalPrice(BigDecimal.ZERO); // Tổng tiền hàng, xử lý sau
            invoice.setTotalDiscount(BigDecimal.ZERO); // Tổng giảm giá: 0 khi nhập kho
            invoice.setCustomerBalance(null); // số dư khách hàng, xử lý sau
            invoice.setTotalPayable(null); // tổng tiền phải trả, xử lý sau
            invoice.setTotalPaid(invoiceDTO.getTotalPaid()); // tổng tiền đã trả
            invoice.setTotalDebt(null); // tổng nợ còn lại, xử lý sau
            invoice.setProcessed(false); // đánh dấu chưa xử lý
            invoice.setSuccess(false); // đánh dấu chưa thành công

            Warehouse warehouse = warehouseRepository.findById(invoiceDTO.getWarehouseId()).orElse(null);
            if (warehouse == null) {
                throw new RuntimeException("Warehouse not found");
            }
            invoice.setWarehouse(warehouse);

            Customer customer = customerRepository.findByIdAndWarehouseId(invoiceDTO.getCustomerId(), invoiceDTO.getWarehouseId()).orElse(null);
            if (customer == null) {
                throw new RuntimeException("Customer not found");
            }
            invoice.setCustomer(customer);

            invoice.setDescription(xssProtectedUtil.htmlToPlainText(invoiceDTO.getDescription()));
            invoice.setShipped(invoiceDTO.getIsShipped());
            invoice = invoiceRepository.save(invoice);

            Invoice finalInvoice = invoice;
            invoiceDTO.getItems().forEach(item -> {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoice(finalInvoice);
                invoiceItem.setPrice(item.getPrice());
                invoiceItem.setQuantity(item.getQuantity());
                invoiceItem.setDiscount(BigDecimal.ZERO);
                invoiceItem.setPayable(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                ProductPackage productPackage = productPackageRepository.findByIdAndWarehouseId(item.getProductPackageId(), invoiceDTO.getWarehouseId());
                if (productPackage == null) {
                    throw new RuntimeException("Product package not found");
                }
                invoiceItem.setProductPackage(productPackage);
                Product product = productRepository.getProductByIdAndWarehouseId(item.getProductId(), invoiceDTO.getWarehouseId());
                if (product == null) {
                    throw new RuntimeException("Product not found");
                }
                invoiceItem.setProduct(product);
                invoiceItem.setProductSnapshotDTO(ProductSnapshotDTO.builder().id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .build()
                );
                invoiceItemRepository.save(invoiceItem);
            });
            return invoice.getId();
        }
        else if (Objects.equals(invoiceDTO.getType(), "SALES")) {
            Invoice invoice = new Invoice();
            invoice.setType(InvoiceType.SALES); // loại hóa đơn
            invoice.setTotalPrice(BigDecimal.ZERO); // Tổng tiền hàng, xử lý sau
            invoice.setTotalDiscount(BigDecimal.ZERO); // Tổng giảm giá: 0 khi nhập kho
            invoice.setCustomerBalance(null); // số dư khách hàng, xử lý sau
            invoice.setTotalPayable(null); // tổng tiền phải trả, xử lý sau
            invoice.setTotalPaid(invoiceDTO.getTotalPaid()); // tổng tiền đã trả
            invoice.setTotalDebt(null); // tổng nợ còn lại, xử lý sau
            invoice.setProcessed(false); // đánh dấu chưa xử lý
            invoice.setSuccess(false); // đánh dấu chưa thành công

            Warehouse warehouse = warehouseRepository.findById(invoiceDTO.getWarehouseId()).orElse(null);
            if (warehouse == null) {
                throw new RuntimeException("Warehouse not found");
            }
            invoice.setWarehouse(warehouse);

            Customer customer = customerRepository.findByIdAndWarehouseId(invoiceDTO.getCustomerId(), invoiceDTO.getWarehouseId()).orElse(null);
            if (customer == null) {
                throw new RuntimeException("Customer not found");
            }
            invoice.setCustomer(customer);

            invoice.setDescription(xssProtectedUtil.htmlToPlainText(invoiceDTO.getDescription()));
            invoice.setShipped(invoiceDTO.getIsShipped());
            invoice = invoiceRepository.save(invoice);

            Invoice finalInvoice = invoice;
            invoiceDTO.getItems().forEach(item -> {



                InvoiceItem invoiceItem = new InvoiceItem();
                Product product = productRepository.getProductByIdAndWarehouseId(item.getProductId(), invoiceDTO.getWarehouseId());
                if (product == null) {
                    throw new RuntimeException("Product not found");
                }
                invoiceItem.setProduct(product);

                ProductPackage productPackage = productPackageRepository.findByIdAndWarehouseId(item.getProductPackageId(), invoiceDTO.getWarehouseId());
                if (productPackage == null) {
                    throw new RuntimeException("Product package not found");
                }
                invoiceItem.setProductPackage(productPackage);

                invoiceItem.setInvoice(finalInvoice);
                invoiceItem.setPrice(product.getPrice()); // giá đề xuất
                invoiceItem.setQuantity(item.getQuantity()); // số lượng
                invoiceItem.setWeight(item.getQuantity() * productPackage.getWeight());
                invoiceItem.setDiscount(item.getDiscount()); // giá thương lượng
                invoiceItem.setPayable(item.getDiscount().multiply(BigDecimal.valueOf((long) item.getQuantity() * productPackage.getWeight())));
                invoiceItem.setProductSnapshotDTO(ProductSnapshotDTO.builder().id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .build()
                );
                invoiceItemRepository.save(invoiceItem);
            });
            return invoice.getId();
        }
        return null;
    }

    @Override
    public Page<InvoiceDetailDTO> searchInvoices(Long warehouseId, InvoiceFilterDTO invoiceFilterDTO, Pageable pageable) {
        Specification<Invoice> specification = InvoiceSpecification.filterInvoices(warehouseId, invoiceFilterDTO);
        return invoiceRepository.findAll(specification, pageable).map(invoice -> InvoiceDetailDTO.builder()
                .id(invoice.getId())
                .type(invoice.getType())
                .totalPrice(invoice.getTotalPrice())
                .totalDiscount(invoice.getTotalDiscount())
                .customerBalance(invoice.getCustomerBalance())
                .totalPayable(invoice.getTotalPayable())
                .totalPaid(invoice.getTotalPaid())
                .totalDebt(invoice.getTotalDebt())
                .customerFullName(invoice.getCustomer().getFullName())
                .customerPhone(invoice.getCustomer().getPhone())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build()
        );
    }

    @Override
    public InvoiceDetailDTO findInvoiceBywarehouseIdAndId(Long warehouseId, Long invoiceId) {
        return invoiceRepository.findByIdAndWarehouseId(invoiceId, warehouseId).map(invoice -> InvoiceDetailDTO.builder()
                .id(invoice.getId())
                .type(invoice.getType())
                .totalPrice(invoice.getTotalPrice())
                .totalDiscount(invoice.getTotalDiscount())
                .customerBalance(invoice.getCustomerBalance())
                .totalPayable(invoice.getTotalPayable())
                .totalPaid(invoice.getTotalPaid())
                .totalDebt(invoice.getTotalDebt())
                .customerFullName(invoice.getCustomer().getFullName())
                .customerPhone(invoice.getCustomer().getPhone())
                .description(invoice.getDescription())
                .isShipped(invoice.isShipped())
                .isProcessed(invoice.isProcessed())
                .isSuccess(invoice.isSuccess())
                .createdAt(invoice.getCreatedAt())
                .createdBy(invoice.getCreatedBy().getId())
                .updatedAt(invoice.getUpdatedAt())
                .build()
        ).orElse(null);
    }
}
