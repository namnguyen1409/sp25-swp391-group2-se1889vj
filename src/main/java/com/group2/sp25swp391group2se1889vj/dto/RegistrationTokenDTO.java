package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

//@Column(name = "email", nullable = false, columnDefinition = "nvarchar(100)")
//    private String email;
//    @Column(name = "token", nullable = false, columnDefinition = "nvarchar(255)")
//    private String token;
//    @Column(name = "role", nullable = false, columnDefinition = "nvarchar(20)")
//    @Enumerated(EnumType.STRING)
//    private RoleType role;
public class RegistrationTokenDTO extends BaseDTO{
    private String email;
    private String token;
    private RoleType role;
}
