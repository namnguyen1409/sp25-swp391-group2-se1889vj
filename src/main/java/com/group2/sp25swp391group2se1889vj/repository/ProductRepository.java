package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findByCreatedById(Long ownerId, Pageable pageable);
    Page<Product> findByCreatedByIdAndNameContainingIgnoreCase(Long ownerId, String name, Pageable pageable);
    Page<Product> findByCreatedByIdAndDescriptionPlainTextContainingIgnoreCase(Long createdById, String descriptionPlainText, Pageable pageable);
    Boolean existsByNameAndCreatedById(String name, Long createdById);

    Product getProductByIdAndWarehouseId(Long productId, Long warehouseId);

    List<Product> findAllByNameContainingAndWarehouseId(String keyword, Long warehouseId);

    boolean existsByNameAndWarehouseId(String name, Long warehouseId);
}

