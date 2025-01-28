package com.group2.sp25swp391group2se1889vj.user.dto;


import com.group2.sp25swp391group2se1889vj.user.roles.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String pass;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean gender;
    private LocalDate dob;
    private String address;
    private RoleType role;
    private boolean isLocked;
    private String lockReason;
}
