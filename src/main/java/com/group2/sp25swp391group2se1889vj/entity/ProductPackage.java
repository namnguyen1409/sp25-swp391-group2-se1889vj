package com.group2.sp25swp391group2se1889vj.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_packages")
public class ProductPackage extends BaseEntity{

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String name;

    @Column(name = "weight", nullable = false)
    private int weight;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;


}
