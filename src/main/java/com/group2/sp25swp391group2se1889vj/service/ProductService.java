package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDTO> findPaginatedProducts(int pageNumber, int pageSize);
    Page<ProductDTO> findPaginatedProductsByOwnerId(Long ownerId, Pageable pageable);
    Page<ProductDTO> findPaginatedProductsByOwnerIdAndNameContaining(Long ownerId, String name, Pageable pageable);
    Page<ProductDTO> findPaginatedProductsByOwnerIdAndDescriptionContaining(Long ownerId, String description, Pageable pageable);
    Page<ProductDTO> findPaginatedProductsByNameContaining(String name, Pageable pageable);

    ProductDTO findProductById(Long id);
    void deleteProductById(Long id);
    Boolean existsByNameAndCreatedById(String name, Long ownerId);

    void saveProduct(ProductDTO productDTO, ProductPackage productPackage);
}
