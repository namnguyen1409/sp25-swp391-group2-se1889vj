package com.group2.sp25swp391group2se1889vj.user.entity;


import com.group2.sp25swp391group2se1889vj.user.roles.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "registration_tokens")
public class RegistrationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    private String email;
    private String token;

    @Enumerated(EnumType.STRING)
    private RoleType role;
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
