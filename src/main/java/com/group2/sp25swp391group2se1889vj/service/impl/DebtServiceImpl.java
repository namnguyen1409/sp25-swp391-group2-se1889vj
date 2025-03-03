package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import com.group2.sp25swp391group2se1889vj.mapper.DebtMapper;
import com.group2.sp25swp391group2se1889vj.repository.CustomerRepository;
import com.group2.sp25swp391group2se1889vj.repository.DebtRepository;
import com.group2.sp25swp391group2se1889vj.service.DebtService;
import com.group2.sp25swp391group2se1889vj.specification.DebtSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;
    private final DebtMapper debtMapper;


    public Page<DebtDTO> searchDebts(Long customerId, DebtFilterDTO debtFilterDTO, Pageable pageable) {
        Specification<Debt> spec = DebtSpecification.filterDebts(customerId, debtFilterDTO);
        return debtRepository.findAll(spec, pageable).map(debtMapper::mapToDebtDTO);
    }

    @Override
    public DebtDTO findDebtById(Long id) {
        return debtRepository.findById(id).map(debtMapper::mapToDebtDTO).orElseThrow(() -> new RuntimeException("Debt not found"));
    }

    @Override
    @Transactional
    public void saveDebt(DebtDTO debtDTO) {
        debtRepository.save(
                debtMapper.mapToDebt(debtDTO).toBuilder()
                        .id(null)
                        .customer(customerRepository.findById(debtDTO.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("Customer not found")))
                        .build()
        );
    }
}
