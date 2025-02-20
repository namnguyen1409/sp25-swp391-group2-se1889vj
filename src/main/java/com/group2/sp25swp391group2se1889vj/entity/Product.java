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
@Table(name = "product")
public class Product extends BaseEntity {
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(18,0)")
    private BigDecimal price;

    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String description;

    @Column(name = "description_plain_text", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String descriptionPlainText;

    @Column(name = "image", nullable = true, columnDefinition = "nvarchar(255)")
    private String image;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<Zone> zones = new HashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<InvoiceItem> invoiceItems = new HashSet<>();

}
