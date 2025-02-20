package com.group2.sp25swp391group2se1889vj.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer extends BaseEntity {
    @Column(name = "full_name", nullable = false, columnDefinition = "nvarchar(255)")
    private String fullName;

    @Column(name = "phone", nullable = false, columnDefinition = "nvarchar(20)")
    private String phone;

    @Column(name = "email", columnDefinition = "nvarchar(255)")
    private String email;

    @Column(name = "address",  columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "customer")
    private Set<Invoice> invoices = new HashSet<>();

}
