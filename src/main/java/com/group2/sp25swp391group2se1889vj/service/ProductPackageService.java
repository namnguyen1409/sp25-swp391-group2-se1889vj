package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ProductPackageDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductPackageFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductPackageService {
    ProductPackageDTO findProductPackageById(Long id);
    void saveProductPackage(ProductPackageDTO productPackageDTO);
    Boolean existsByNameAndCreatedById(String name, Long createdById);

    void addProductPackage(ProductPackageDTO productPackageDTO);

    Page<ProductPackageDTO> searchProductPackages(Long warehouseId, ProductPackageFilterDTO productPackageFilterDTO, Pageable pageable);

    List<ProductPackageDTO> searchProductPackages(Long warehouseId, String name);

    ProductPackage findByIdAndWarehouseId(Long productPackageId, Long warehouseId);

    List<ProductPackageDTO> getAllProductPackages(Long warehouseId);
}
