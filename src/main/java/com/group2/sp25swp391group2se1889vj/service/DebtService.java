package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface DebtService {
    Page<DebtDTO> findPaginatedDebtsByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    Page<DebtDTO> findPaginatedDebtsByCustomerIdAndDescriptionContainingIgnoreCase(Long customerId, String description, Pageable pageable);
    Page<DebtDTO> findPaginatedDebtsByCustomerId(Long customerId, Pageable pageable);
    void saveDebt(DebtDTO debtDTO);
    Page<DebtDTO> searchDebts(Long customerId, DebtFilterDTO debtFilterDTO, Pageable pageable);
    DebtDTO findDebtById(Long id);
}
