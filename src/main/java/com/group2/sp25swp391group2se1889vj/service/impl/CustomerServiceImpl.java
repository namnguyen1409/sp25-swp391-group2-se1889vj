package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import com.group2.sp25swp391group2se1889vj.exception.CustomerNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.CustomerMapper;
import com.group2.sp25swp391group2se1889vj.repository.CustomerRepository;
import com.group2.sp25swp391group2se1889vj.service.CustomerService;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;
    private MessageService messageService;

    @Override
    public Page<CustomerDTO> findPaginatedCustomers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Customer> page = customerRepository.findAll(pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public Page<CustomerDTO> findPaginatedCustomersByFullNameContaining(String fullName, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Customer> page = customerRepository.findByFullNameContaining(fullName, pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public Page<CustomerDTO> findPaginatedCustomersByCreatedById(Long createdById, Pageable pageable) {
        Page<Customer> page = customerRepository.findByCreatedById(createdById, pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public CustomerDTO findCustomerById(Long id) {
        try {
            Optional<Customer> customerOptional = customerRepository.findById(id);
            return customerOptional.map(customer -> customerMapper.mapToCustomerDTO(customer)).orElse(null);
        } catch (NoSuchElementException exception) {
            String message = messageService.getMessage("entity.notfound", id);
            throw new CustomerNoSuchElementException(message, id);
        }
    }

    @Override
    public CustomerDTO findCustomerByPhone(String phone) {
        Optional<Customer> customerOptional = customerRepository.findByPhone(phone);
        return customerOptional.map(customer -> customerMapper.mapToCustomerDTO(customer)).orElse(null);
    }

    @Override
    public void saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.mapToCustomer(customerDTO);
        customerRepository.save(customer);
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
    public Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndFullNameContaining(Long createdById, String fullName, Pageable pageable) {
        Page<Customer> page = customerRepository.findByCreatedByIdAndFullNameContaining(createdById, fullName, pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndPhoneContaining(Long createdById, String phone, Pageable pageable) {
        Page<Customer> page = customerRepository.findByCreatedByIdAndPhoneContaining(createdById, phone, pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndEmailContaining(Long createdById, String email, Pageable pageable) {
        Page<Customer> page = customerRepository.findByCreatedByIdAndEmailContaining(createdById, email, pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }

    @Override
    public Page<CustomerDTO> findPaginatedCustomersByCreatedByIdAndAddressContaining(Long createdById, String address, Pageable pageable) {
        Page<Customer> page = customerRepository.findByCreatedByIdAndAddressContaining(createdById, address, pageable);
        return page.map(customerMapper::mapToCustomerDTO);
    }
}
