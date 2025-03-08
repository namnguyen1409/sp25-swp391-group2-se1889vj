package com.group2.sp25swp391group2se1889vj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Table(name = "warehouses")
public class Warehouse extends BaseEntity{
    @EqualsAndHashCode.Include
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

    @EqualsAndHashCode.Include
    @Column(name = "location", nullable = false, columnDefinition = "nvarchar(255)")
    private String location;

    @EqualsAndHashCode.Include
    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(max)")
    private String description;

    @OneToOne
    @JoinColumn(name="owner_id", referencedColumnName = "id")
    private User owner;

    @OneToMany(mappedBy = "assignedWarehouse", fetch = FetchType.LAZY)
    private Set<User> staff = new HashSet<>();
}
