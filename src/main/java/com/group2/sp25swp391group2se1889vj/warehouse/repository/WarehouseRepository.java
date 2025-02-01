package com.group2.sp25swp391group2se1889vj.warehouse.repository;

import com.group2.sp25swp391group2se1889vj.warehouse.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Warehouse> findByOwnerIdAndNameContaining(Long ownerId, String name, Pageable pageable);
    Page<Warehouse> findByOwnerIdAndLocationContaining(Long ownerId, String location, Pageable pageable);
    Warehouse findByOwnerIdAndName(Long ownerId, String name);
}
