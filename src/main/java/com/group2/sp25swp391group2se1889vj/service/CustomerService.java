package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.CustomerFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
public interface CustomerService {
    CustomerDTO findCustomerById(Long id);
    void saveCustomer(CustomerDTO customerDTO);
    void updateCustomer(CustomerDTO customerDTO);
    Boolean existByPhone(String phone);
    Boolean existByEmail(String email);
    Boolean existByPhoneAndIdNot(String phone, Long id);
    Boolean existByEmailAndIdNot(String email, Long id);
    void addBalance(Long id, BigDecimal balance);
    void subtractBalance(Long id, BigDecimal balance);
    Page<CustomerDTO> searchCustomers(Long customerId, CustomerFilterDTO customerFilterDTO, Pageable pageable);
}
