package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String token);
    RefreshToken findByToken(String token);
    RefreshToken findByTokenAndUserId(String token, Long userId);
}
