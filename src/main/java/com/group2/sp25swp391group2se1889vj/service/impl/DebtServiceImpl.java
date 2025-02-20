package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import com.group2.sp25swp391group2se1889vj.mapper.DebtMapper;
import com.group2.sp25swp391group2se1889vj.repository.CustomerRepository;
import com.group2.sp25swp391group2se1889vj.repository.DebtRepository;
import com.group2.sp25swp391group2se1889vj.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;
    private final DebtMapper debtMapper;

    @Override
    public Page<DebtDTO> findPaginatedDebtsByDescriptionContainingIgnoreCase(String description, Pageable pageable) {
        return debtRepository.findPaginatedDebtsByDescriptionContainingIgnoreCase(description, pageable).map(debtMapper::mapToDebtDTO);
    }

    @Override
    public Page<DebtDTO> findPaginatedDebtsByCustomerIdAndDescriptionContainingIgnoreCase(Long customerId, String description, Pageable pageable) {
        return debtRepository.findPaginatedDebtsByCustomerIdAndDescriptionContainingIgnoreCase(customerId, description, pageable).map(debtMapper::mapToDebtDTO);
    }

    @Override
    public Page<DebtDTO> findPaginatedDebtsByCustomerId(Long customerId, Pageable pageable) {
        return debtRepository.findPaginatedDebtsByCustomerId(customerId, pageable).map(debtMapper::mapToDebtDTO);
    }

    @Override
    @Transactional
    public void saveDebt(DebtDTO debtDTO) {
        Debt debt = debtMapper.mapToDebt(debtDTO);
        debt.setId(null);
        Customer customer = customerRepository.findById(debtDTO.getCustomerId()).orElseThrow(() -> new RuntimeException("Customer not found"));
        debt.setCustomer(customer);
        if (debt.getType().equals(DebtType.ADD)) {
            customer.setBalance(customer.getBalance().add(debt.getAmount()));
        } else {
            customer.setBalance(customer.getBalance().subtract(debt.getAmount()));
        }
        customerRepository.save(customer);
        debtRepository.save(debt);
    }
}
