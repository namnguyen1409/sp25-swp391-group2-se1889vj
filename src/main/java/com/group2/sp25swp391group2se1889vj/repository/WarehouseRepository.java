package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByOwnerId(Long ownerId);

    Warehouse findWarehouseById(Long id);
}
