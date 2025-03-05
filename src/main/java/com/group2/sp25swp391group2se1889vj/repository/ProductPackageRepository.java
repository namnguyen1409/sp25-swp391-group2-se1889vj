package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPackageRepository extends JpaRepository<ProductPackage, Long> , JpaSpecificationExecutor<ProductPackage> {
    Boolean existsByNameAndCreatedById(String name, Long createdById);

}
