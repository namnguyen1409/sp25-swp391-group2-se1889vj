package com.group2.sp25swp391group2se1889vj.warehouse.entity;


import com.group2.sp25swp391group2se1889vj.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên khu vực trong kho
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

    // Mô tả về khu vực trong kho
    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(255)")
    private String description;

    // Trạng thái xóa khu vực
    @Column(name = "is_deleted", nullable = false, columnDefinition = "bit")
    private boolean isDeleted = false;

    // Thời gian tạo khu vực
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Kho chứa chứa khu vực
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    // nhiều khu vực có thể chứa cùng một loại gạo
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    // Số lượng gạo trong khu vực (đơn vị: kg)
    @Column(name = "quantity", nullable = false, columnDefinition = "int")
    private int quantity;

}
