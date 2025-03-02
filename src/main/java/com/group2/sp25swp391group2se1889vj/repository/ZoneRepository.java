package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Page<Zone> findByWarehouseId(Long warehouseId, Pageable pageable);
    Page<Zone> findByWarehouseIdAndNameContaining(Long warehouseId, String name, Pageable pageable);
    Page<Zone> findByWarehouseIdAndProductNameContaining(Long warehouseId, String productName, Pageable pageable);
    List<Zone> findByWarehouseId(Long warehouseId);
    Boolean existsByWarehouseIdAndName(Long warehouseId, String name);
}
