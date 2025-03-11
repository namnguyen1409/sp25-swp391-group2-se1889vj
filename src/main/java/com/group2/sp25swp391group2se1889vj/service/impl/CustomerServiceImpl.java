package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.CustomerFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import com.group2.sp25swp391group2se1889vj.exception.CustomerNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.CustomerMapper;
import com.group2.sp25swp391group2se1889vj.repository.CustomerRepository;
import com.group2.sp25swp391group2se1889vj.service.CustomerService;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import com.group2.sp25swp391group2se1889vj.specification.CustomerSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;
    private MessageService messageService;


    @Override
    public CustomerDTO findCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::mapToCustomerDTO)
                .orElseThrow(() -> new CustomerNoSuchElementException(messageService.getMessage("entity.notfound", id), id));
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.mapToCustomer(customerDTO);
        Customer customer1 = customerRepository.save(customer);
        return customerMapper.mapToCustomerDTO(customer1);
    }

    @Override
    @Transactional
    public void updateCustomer(CustomerDTO customerDTO) {
        Optional<Customer> customerOptional = customerRepository.findById(customerDTO.getId());
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setFullName(customerDTO.getFullName());
            customer.setPhone(customerDTO.getPhone());
            customer.setEmail(customerDTO.getEmail());
            customer.setAddress(customerDTO.getAddress());
            customerRepository.save(customer);
        } else {
            String message = messageService.getMessage("entity.notfound", customerDTO.getId());
            throw new CustomerNoSuchElementException(message, customerDTO.getId());
        }
    }

    @Override
    public Boolean existByPhoneAndWarehouseId(String phone, Long warehouseId) {
        return customerRepository.existsByPhoneAndWarehouseId(phone, warehouseId);
    }

    @Override
    public Boolean existByEmailAndWarehouseId(String email, Long warehouseId) {
        return customerRepository.existsByEmailAndWarehouseId(email, warehouseId);
    }

    @Override
    public Boolean existByPhoneAndWarehouseIdAndIdNot(String phone, Long warehouseId, Long id) {
        return customerRepository.existsByPhoneAndWarehouseIdAndIdNot(phone, warehouseId, id);
    }

    @Override
    public Boolean existByEmailAndWarehouseIdAndIdNot(String email, Long warehouseId, Long id) {
        return customerRepository.existsByEmailAndWarehouseIdAndIdNot(email, warehouseId, id);
    }


    @Override
    public void addBalance(Long id, BigDecimal balance) {
        customerRepository.increaseBalance(id, balance);
    }

    @Override
    public void subtractBalance(Long id, BigDecimal balance) {
        customerRepository.decreaseBalance(id, balance);
    }

    @Override
    public Page<CustomerDTO> searchCustomers(Long warehouseId, CustomerFilterDTO customerFilterDTO, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.filterCustomers(warehouseId, customerFilterDTO);
        return customerRepository.findAll(spec, pageable).map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public CustomerDTO findCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone)
                .map(customerMapper::mapToCustomerDTO).orElse(null);
    }

    @Override
    public CustomerDTO findCustomerByPhoneAndWarehouseId(String phone, Long warehouseId) {
        return customerRepository.findByPhoneAndWarehouseId(phone, warehouseId)
                .map(customerMapper::mapToCustomerDTO).orElse(null);
    }


}
