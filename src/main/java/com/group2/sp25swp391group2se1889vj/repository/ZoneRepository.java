package com.group2.sp25swp391group2se1889vj.repository;


import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long>, JpaSpecificationExecutor<Zone> {
    List<Zone> findAllByNameContainingAndWarehouseId(String keyword, Long warehouseId);
}
