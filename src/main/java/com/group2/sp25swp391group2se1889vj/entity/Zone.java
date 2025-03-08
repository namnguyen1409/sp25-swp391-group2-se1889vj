package com.group2.sp25swp391group2se1889vj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Table(name = "zones")
public class Zone extends BaseEntity{

    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

    @EqualsAndHashCode.Include
    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
