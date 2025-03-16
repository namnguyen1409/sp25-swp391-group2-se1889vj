package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO extends BaseFilterDTO{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean gender;
    @DateTimeFormat( pattern = "dd/MM/yyyy")
    private LocalDate birthday;
    private String address;
    private Long warehouseId;
    private Long ownerId;
    private RoleType role;
    private boolean locked;
    private String lockReason;
}
