package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserDetailsDTO {
    private Long id;
    private String username;
    private String pass;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean gender;
    private LocalDate birthday;
    private String address;
    private Long warehouseId;
    private RoleType role;
    private boolean locked;
    private String lockReason;
    private LocalDateTime createAt;
    private String avatar;



//    private Set<InventoryDTO> inventories;
//
//    private InventoryDTO assignedInventory;

}
