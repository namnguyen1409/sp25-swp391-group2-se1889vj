package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>, JpaSpecificationExecutor<Debt> {
    Page<Debt> findPaginatedDebtsByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    Page<Debt> findPaginatedDebtsByCustomerIdAndDescriptionContainingIgnoreCase(Long customerId, String description, Pageable pageable);
    Page<Debt> findPaginatedDebtsByCustomerId(Long customerId, Pageable pageable);
    List<Debt> findDebtsByIsProcessedIsFalse();
}
