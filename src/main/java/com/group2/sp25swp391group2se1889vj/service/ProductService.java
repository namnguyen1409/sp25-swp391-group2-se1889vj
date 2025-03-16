package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    List<ProductDTO> searchProducts(Long warehouseId, String keyword);

    Page<ProductDTO> searchProducts(Long warehouseId, ProductFilterDTO productFilterDTO, Pageable pageable);

    void addProduct(ProductDTO productDTO) throws Exception;

    ProductDTO findProductByIdAndWarehouseId(Long id, Long warehouseId);
}
