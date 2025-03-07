package com.group2.sp25swp391group2se1889vj.repository;


import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long>, JpaSpecificationExecutor<Zone> {
    Page<Zone> findByCreatedBy(User createdBy, Pageable pageable);
    Page<Zone> findByCreatedByContainingAndNameContaining(User createdBy, String name, Pageable pageable);
    Page<Zone> findByCreatedByContainingAndProductNameContaining(User createdBy, String productName, Pageable pageable);
    Page<Zone> findByCreatedByContainingAndNameContainingAndProductNameContaining(User createdBy, String name, String productName, Pageable pageable);
}
