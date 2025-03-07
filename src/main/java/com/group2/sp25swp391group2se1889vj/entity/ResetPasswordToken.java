package com.group2.sp25swp391group2se1889vj.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reset_password_tokens")
public class ResetPasswordToken extends BaseEntity {

    @OneToOne
    private User user;

    @Column(name = "token", nullable = false, columnDefinition = "nvarchar(255)")
    private String token;

}
