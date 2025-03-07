package com.group2.sp25swp391group2se1889vj.repository;


import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;


public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findAllByNameContainingAndWarehouseId(String keyword, Long warehouseId);
}
