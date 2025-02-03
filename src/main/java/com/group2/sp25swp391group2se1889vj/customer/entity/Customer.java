package com.group2.sp25swp391group2se1889vj.customer.entity;

import com.group2.sp25swp391group2se1889vj.invoice.entity.Invoice;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, columnDefinition = "nvarchar(255)")
    private String fullName;

    @Column(name = "phone", nullable = true, columnDefinition = "nvarchar(20)")
    private String phone;

    @Column(name = "email", nullable = true, columnDefinition = "nvarchar(255)")
    private String email;

    @Column(name = "address", nullable = true, columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "customer")
    private Set<Invoice> invoices = new HashSet<>();

}
