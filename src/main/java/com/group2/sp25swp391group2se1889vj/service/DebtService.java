package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DebtService {
    void saveDebt(DebtDTO debtDTO);
    Page<DebtDTO> searchDebts(Long customerId, DebtFilterDTO debtFilterDTO, Pageable pageable);
    DebtDTO findDebtById(Long id);
}
