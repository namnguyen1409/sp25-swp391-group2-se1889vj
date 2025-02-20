package com.group2.sp25swp391group2se1889vj.entity;


import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_tokens")
public class RegistrationToken extends BaseEntity {
    private String email;
    private String token;
    @Enumerated(EnumType.STRING)
    private RoleType role;
}
