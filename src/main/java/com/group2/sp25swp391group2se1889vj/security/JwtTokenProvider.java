package com.group2.sp25swp391group2se1889vj.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Data
public class JwtTokenProvider {



    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + Long.parseLong(expiration));
        SecretKey key = generateKey();
        return Jwts.builder()
                .subject(Long.toString(userDetails.getUser().getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        var claims = getClaimsFromToken(token);
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            getClaimsFromToken(authToken);
            return true;
        } catch(SecurityException | MalformedJwtException e) {
            System.out.println("JWT was expired or incorrect");
        } catch (ExpiredJwtException e) {
            System.out.println("JWT was expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT was unsupported");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT was empty");
        }
        return false;
    }


    private Claims getClaimsFromToken(String token) {
        SecretKey key = generateKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey generateKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
