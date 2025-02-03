package com.group2.sp25swp391group2se1889vj.warehouse.entity;

import com.group2.sp25swp391group2se1889vj.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String name;

    @Column(name = "location", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String location;

    @Column(name = "description", nullable = false, columnDefinition = "NVARCHAR(max)")
    private String description;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "isDeleted", nullable = false, columnDefinition = "bit")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private User owner;

}
