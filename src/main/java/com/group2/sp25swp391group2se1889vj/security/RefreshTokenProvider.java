package com.group2.sp25swp391group2se1889vj.security;

import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshTokenProvider {

    @Value("${refresh.token.expiration}")
    private String refreshTokenExpiration;

    private final EncryptionUtil encryptionUtil;

    public RefreshTokenProvider(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }


    public String generateRefreshToken(String key) {
        String data = key + ":" + Instant.now().plusSeconds(Long.parseLong(refreshTokenExpiration)).toEpochMilli();
        try {
            return encryptionUtil.encrypt(data);
        } catch (Exception e) {
            return null;
        }
    }

    public String getKeyFromRefreshToken(String refreshToken) {
        try {
            String data = encryptionUtil.decrypt(refreshToken);
            return data.split(":")[0];
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            String data = encryptionUtil.decrypt(refreshToken);
            long expiration = Long.parseLong(data.split(":")[1]);
            return Instant.now().toEpochMilli() < expiration;
        } catch (Exception e) {
            return false;
        }
    }

}
