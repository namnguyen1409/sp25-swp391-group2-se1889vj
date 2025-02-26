package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.CustomerFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
public interface CustomerService {
    Page<CustomerDTO> findPaginatedCustomers(int pageNumber, int pageSize);
    Page<CustomerDTO> findPaginatedCustomersByFullNameContaining(String fullName, int pageNumber, int pageSize);
    Page<CustomerDTO> findPaginatedCustomersByCreatedById(Long createdById, Pageable pageable);
    CustomerDTO findCustomerById(Long id);
    CustomerDTO findCustomerByPhone(String phone);
    void saveCustomer(CustomerDTO customerDTO);
    void updateCustomer(CustomerDTO customerDTO);
    Boolean existByPhone(String phone);
    Boolean existByEmail(String email);
    Boolean existByPhoneAndIdNot(String phone, Long id);
    Boolean existByEmailAndIdNot(String email, Long id);
    Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndFullNameContaining(Long createdById, String fullName, Pageable pageable);
    Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndPhoneContaining(Long createdById, String phone, Pageable pageable);
    Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndEmailContaining(Long createdById, String email, Pageable pageable);
    Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndAddressContaining(Long createdById, String address, Pageable pageable);
    void addBalance(Long id, BigDecimal balance);
    void subtractBalance(Long id, BigDecimal balance);
    Page<CustomerDTO> searchCustomers(Long customerId, CustomerFilterDTO customerFilterDTO, Pageable pageable);
}
