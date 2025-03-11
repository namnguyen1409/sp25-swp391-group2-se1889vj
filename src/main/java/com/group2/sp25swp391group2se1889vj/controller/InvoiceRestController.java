package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/invoice")
@AllArgsConstructor
public class InvoiceRestController {

    private final InvoiceService invoiceService;

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


    @PostMapping("/purchase")
    public ResponseEntity<Long> createOrder(
            @RequestBody InvoiceDTO invoiceDTO
            ) {
        invoiceDTO.setWarehouseId(getWarehouseId());
        var result = invoiceService.createInvoice(invoiceDTO);
        return ResponseEntity.ok(result);
    }
}
