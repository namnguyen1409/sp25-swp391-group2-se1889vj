package com.group2.sp25swp391group2se1889vj.dto;


import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseDTO{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean gender;
    private LocalDate birthday;
    private String address;
    private RoleType role;
    private boolean isLocked;
    private String lockReason;
}
