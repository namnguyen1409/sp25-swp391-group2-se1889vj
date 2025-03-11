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
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    void updateCustomer(CustomerDTO customerDTO);

    Boolean existByPhoneAndWarehouseId(String phone, Long warehouseId);

    Boolean existByEmailAndWarehouseId(String email, Long warehouseId);

    Boolean existByPhoneAndWarehouseIdAndIdNot(String phone, Long warehouseId, Long id);

    Boolean existByEmailAndWarehouseIdAndIdNot(String email, Long warehouseId, Long id);

    void addBalance(Long id, BigDecimal balance);
    void subtractBalance(Long id, BigDecimal balance);
    Page<CustomerDTO> searchCustomers(Long warehouseId, CustomerFilterDTO customerFilterDTO, Pageable pageable);

    CustomerDTO findCustomerByPhone(String phone);

    CustomerDTO findCustomerByPhoneAndWarehouseId(String phone, Long warehouseId);

}
