package com.group2.sp25swp391group2se1889vj.product.entity;


import com.group2.sp25swp391group2se1889vj.user.entity.User;
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
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(50)")
    private String name;

    @Column(name = "type", nullable = false, columnDefinition = "nvarchar(50)")
    private String type;

    @Column(name = "variety", nullable = false, columnDefinition = "nvarchar(50)")
    private String variety;

    @Column(name = "origin", nullable = false, columnDefinition = "nvarchar(50)")
    private String origin;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(18,0)")
    private BigDecimal price;

    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String description;

    @Column(name = "description_plain_text", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String descriptionPlainText;

    @Column(name = "image", nullable = true, columnDefinition = "nvarchar(255)")
    private String image;


    @ManyToOne
    @JoinColumn(name = "product_owner_id", nullable = false)
    private User productOwner;


}
