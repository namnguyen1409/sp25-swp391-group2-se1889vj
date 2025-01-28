package com.group2.sp25swp391group2se1889vj.user.repository;

import com.group2.sp25swp391group2se1889vj.user.entity.RegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
    RegistrationToken findByToken(String token);
    RegistrationToken findByEmail(String email);
    void deleteByToken(String token);
}
