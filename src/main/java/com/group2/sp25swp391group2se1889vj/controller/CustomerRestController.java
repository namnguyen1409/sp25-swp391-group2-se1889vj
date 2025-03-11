package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Long getWarehouseId() {
        var currentUser = getUser();
        if(currentUser.getRole() == RoleType.OWNER) return currentUser.getWarehouse().getId();
        else return currentUser.getAssignedWarehouse().getId();
    }


    @GetMapping("/search")
    public ResponseEntity<CustomerDTO> searchCustomers(
            @RequestParam(value = "phone") String phone
    ) {
        var customer = customerService.findCustomerByPhoneAndWarehouseId(phone, getWarehouseId());
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }


    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(
            @Validated  @RequestBody CustomerDTO customerDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Long warehouseId = getWarehouseId();
        if (customerService.existByPhoneAndWarehouseId(customerDTO.getPhone(), warehouseId)) {
            return ResponseEntity.badRequest().body("Phone number already exists");
        }
        if(customerDTO.getEmail()!= null && Boolean.TRUE.equals(customerService.existByEmailAndWarehouseId(customerDTO.getEmail(), warehouseId)) && !customerDTO.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        customerDTO.setWarehouseId(warehouseId);
        CustomerDTO customer = customerService.saveCustomer(customerDTO);
        return ResponseEntity.ok(customer);
    }
}
