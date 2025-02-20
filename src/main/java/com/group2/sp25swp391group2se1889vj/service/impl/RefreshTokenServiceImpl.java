package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.entity.RefreshToken;
import com.group2.sp25swp391group2se1889vj.mapper.RefreshTokenMapper;
import com.group2.sp25swp391group2se1889vj.repository.RefreshTokenRepository;
import com.group2.sp25swp391group2se1889vj.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

        private final RefreshTokenRepository refreshTokenRepository;
        private final RefreshTokenMapper refreshTokenMapper;

        @Override
        public void deleteByToken(String token) {
            refreshTokenRepository.deleteByToken(token);
        }

        @Override
        public void saveRefreshToken(RefreshToken refreshToken) {
            refreshTokenRepository.save(refreshToken);
        }

        @Override
        public boolean validateRefreshToken(String token, Long userId) {
            return refreshTokenRepository.findByTokenAndUserId(token, userId) != null;
        }
}
