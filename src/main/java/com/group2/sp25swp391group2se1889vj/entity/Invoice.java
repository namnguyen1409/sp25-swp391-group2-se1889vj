package com.group2.sp25swp391group2se1889vj.entity;

import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice extends BaseEntity{
    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "nvarchar(10)")
    private InvoiceType type;

    // tổng tiền hàng
    @EqualsAndHashCode.Include
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    // tổng giảm giá
    @EqualsAndHashCode.Include
    @Column(name = "total_discount")
    private BigDecimal totalDiscount;

    // số dư khách hàng
    @EqualsAndHashCode.Include
    @Column(name = "customer_balance")
    private BigDecimal customerBalance;

    // tổng tiền phải trả
    @EqualsAndHashCode.Include
    @Column(name="total_payable")
    private BigDecimal totalPayable;

    // tổng tiền đã trả
    @EqualsAndHashCode.Include
    @Column(name = "total_paid")
    private BigDecimal totalPaid;

    // tổng nợ còn lại
    @EqualsAndHashCode.Include
    @Column(name = "total_debt")
    private BigDecimal totalDebt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "is_shipped", nullable = false)
    private boolean isShipped;

    private boolean isProcessed = false;
    private boolean isSuccess = false;

    @Column(name = "is_warning", nullable = true)
    private boolean isWarning = false;

    // invoice Items

    @OneToMany(mappedBy = "invoice")
    private Set<InvoiceItem> invoiceItems;

}
