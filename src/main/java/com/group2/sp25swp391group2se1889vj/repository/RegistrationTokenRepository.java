package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.RegistrationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
    Page<RegistrationToken> findByEmailContaining(String email, Pageable pageable);
    Page<RegistrationToken> findByTokenContaining(String token, Pageable pageable);

    RegistrationToken findByToken(String token);
    RegistrationToken findByEmail(String email);
    void deleteByToken(String token);

    void deleteById(Long id);
    Optional<RegistrationToken> findById(Long id);
}
