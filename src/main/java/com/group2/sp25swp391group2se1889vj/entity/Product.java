package com.group2.sp25swp391group2se1889vj.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {
    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

    @EqualsAndHashCode.Include
    @Column(name = "price", nullable = false, columnDefinition = "decimal(18,0)")
    private BigDecimal price;

    // khối lượng tồn kho của sản phẩm (kg)
    @EqualsAndHashCode.Include
    @Column(name = "stock_quantity", nullable = false, columnDefinition = "int")
    private Integer stockQuantity = 0;

    @ManyToOne
    @JoinColumn(name="product_package_id", nullable = false)
    private ProductPackage productPackage;

    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String description;

    @Column(name = "description_plain_text", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String descriptionPlainText;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "image", nullable = true, columnDefinition = "nvarchar(255)")
    private String image;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Zone> zones = new HashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<InvoiceItem> invoiceItems = new HashSet<>();

}
