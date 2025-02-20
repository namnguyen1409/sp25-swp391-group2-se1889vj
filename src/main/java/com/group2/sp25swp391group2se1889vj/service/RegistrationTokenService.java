package com.group2.sp25swp391group2se1889vj.service;

public interface RegistrationTokenService {
    void saveToken(String token, Long userId);
    void deleteToken(String token);
    Long getUserIdByToken(String token);
}
