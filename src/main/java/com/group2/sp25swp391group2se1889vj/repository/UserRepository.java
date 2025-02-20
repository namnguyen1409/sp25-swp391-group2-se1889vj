package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Optional<User> findByRefreshTokens_Token(String token);
}
