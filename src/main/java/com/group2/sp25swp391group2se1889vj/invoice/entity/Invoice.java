package com.group2.sp25swp391group2se1889vj.invoice.entity;

import com.group2.sp25swp391group2se1889vj.customer.entity.Customer;
import com.group2.sp25swp391group2se1889vj.invoice.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "nvarchar(10)")
    private InvoiceType type;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "total_discount", nullable = false)
    private BigDecimal totalDiscount;

    @Column(name = "total_paid", nullable = false)
    private BigDecimal totalPaid;

    @Column(name = "total_debt", nullable = false)
    private BigDecimal totalDebt;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(255)")
    private String description;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "is_shipped", nullable = false)
    private boolean isShipped;

}
