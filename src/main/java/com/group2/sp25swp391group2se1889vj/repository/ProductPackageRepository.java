package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ProductPackageRepository extends JpaRepository<ProductPackage, Long> , JpaSpecificationExecutor<ProductPackage> {
    Boolean existsByNameAndCreatedById(String name, Long createdById);

    List<ProductPackage> findAllByNameContainingAndWarehouseId(String name, Long warehouseId);

    ProductPackage findByIdAndWarehouseId(Long productPackageId, Long warehouseId);

    List<ProductPackage> findAllByWarehouseId(Long warehouseId);
}
