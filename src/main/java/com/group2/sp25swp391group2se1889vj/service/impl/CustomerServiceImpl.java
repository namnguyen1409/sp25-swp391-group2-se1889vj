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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
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
    public void saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.mapToCustomer(customerDTO);
        customerRepository.save(customer);
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
    public Boolean existByPhone(String phone) {
        return customerRepository.existsByPhone(phone);
    }

    @Override
    public Boolean existByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public Boolean existByPhoneAndIdNot(String phone, Long id) {
        return customerRepository.existsByPhoneAndIdNot(phone, id);
    }

    @Override
    public Boolean existByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
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
    public Page<CustomerDTO> searchCustomers(Long customerId, CustomerFilterDTO customerFilterDTO, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.filterCustomers(customerId, customerFilterDTO);
        return customerRepository.findAll(spec, pageable).map(customerMapper::mapToCustomerDTO);
    }


}
