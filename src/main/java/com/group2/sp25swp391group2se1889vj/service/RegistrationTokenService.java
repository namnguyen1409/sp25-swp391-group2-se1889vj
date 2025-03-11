package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.RegistrationTokenDTO;
import com.group2.sp25swp391group2se1889vj.entity.RegistrationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegistrationTokenService {
    void saveToken(String token, Long userId);
    void deleteToken(String token);
    Long getUserIdByToken(String token);
    Page<RegistrationTokenDTO> findPaginationRegisterTokenDTO(Pageable pageable);
    public void resendEmailInvitation(Long id);

    Page<RegistrationTokenDTO> findByEmailContaining(String email, Pageable pageable);
    Page<RegistrationTokenDTO> findByTokenContaining(String token, Pageable pageable);
}

