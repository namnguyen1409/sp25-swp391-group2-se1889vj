package com.group2.sp25swp391group2se1889vj.repository;


import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long>, JpaSpecificationExecutor<Zone> {
    List<Zone> findAllByNameContainingAndWarehouseId(String keyword, Long warehouseId);

    Zone findByIdAndWarehouseId(Long zoneId, Long warehouseId);

    @Override
    @EntityGraph(attributePaths = {"product"})
    Page<Zone> findAll(Specification<Zone> spec, Pageable pageable);
}
