package com.group2.sp25swp391group2se1889vj.mapper;

import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import com.group2.sp25swp391group2se1889vj.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DebtMapper {
    private final ModelMapper modelMapper;
    public DebtDTO mapToDebtDTO(Debt debt) {
        return modelMapper.map(debt, DebtDTO.class);
    }

    public Debt mapToDebt(DebtDTO debtDTO) {
        return modelMapper.map(debtDTO, Debt.class);
    }
}
