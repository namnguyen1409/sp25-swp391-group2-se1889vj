package com.group2.sp25swp391group2se1889vj.mapper;


import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final ModelMapper modelMapper;

    public CustomerDTO mapToCustomerDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer mapToCustomer(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }
}
