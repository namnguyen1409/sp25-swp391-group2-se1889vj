package com.group2.sp25swp391group2se1889vj.entity;


import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_tokens")
public class RegistrationToken extends BaseEntity {
    @Column(name = "email", nullable = false, columnDefinition = "nvarchar(100)")
    private String email;
    @Column(name = "token", nullable = false, columnDefinition = "nvarchar(255)")
    private String token;
    @Column(name = "role", nullable = false, columnDefinition = "nvarchar(20)")
    @Enumerated(EnumType.STRING)
    private RoleType role;
}
