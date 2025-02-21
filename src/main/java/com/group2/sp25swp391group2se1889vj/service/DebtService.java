package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DebtService {
    Page<DebtDTO> findPaginatedDebtsByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    Page<DebtDTO> findPaginatedDebtsByCustomerIdAndDescriptionContainingIgnoreCase(Long customerId, String description, Pageable pageable);
    Page<DebtDTO> findPaginatedDebtsByCustomerId(Long customerId, Pageable pageable);
    void saveDebt(DebtDTO debtDTO);
}
