package com.group2.sp25swp391group2se1889vj.entity;

import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice extends BaseEntity{
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "nvarchar(10)")
    private InvoiceType type;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "total_discount", nullable = false)
    private BigDecimal totalDiscount;

    @Column(name="total_payable", nullable = false)
    private BigDecimal totalPayable;

    @Column(name = "total_paid", nullable = false)
    private BigDecimal totalPaid;

    @Column(name = "total_debt", nullable = false)
    private BigDecimal totalDebt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "is_shipped", nullable = false)
    private boolean isShipped;


}
