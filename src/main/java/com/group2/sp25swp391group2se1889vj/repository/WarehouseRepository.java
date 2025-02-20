package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByCreatedById(Long ownerId, Pageable pageable);
    Page<Warehouse> findByCreatedByIdAndNameContaining(Long ownerId, String name, Pageable pageable);
    Page<Warehouse> findByCreatedByIdAndLocationContaining(Long ownerId, String location, Pageable pageable);
    Warehouse findByCreatedByIdAndName(Long ownerId, String name);
}
