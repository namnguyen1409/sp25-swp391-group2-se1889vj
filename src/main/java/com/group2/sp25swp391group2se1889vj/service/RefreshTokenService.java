package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.entity.RefreshToken;

public interface RefreshTokenService {
    void deleteByToken(String token);
    void saveRefreshToken(RefreshToken refreshToken);
    boolean validateRefreshToken(String token, Long userId);
}
