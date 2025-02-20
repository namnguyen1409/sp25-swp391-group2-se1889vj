package com.group2.sp25swp391group2se1889vj.mapper;

import com.group2.sp25swp391group2se1889vj.dto.RefreshTokenDTO;
import com.group2.sp25swp391group2se1889vj.entity.RefreshToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {
    ModelMapper modelMapper = new ModelMapper();

    /**
     * Tu dong anh xa RefreshToken thanh RefreshTokenDTO
     * thay vi phai set tung thuoc tinh
     * @param refreshToken
     * @return RefreshTokenDTO
     * .builder() dung de khoi tao doi tuong RefreshTokenDTO va tra ve chinh no sau moi lan cham
     * .build() dung de hoan tat va tra ve doi tuong RefreshTokenDTO
     */
    public RefreshTokenDTO mapToRefreshTokenDTO(RefreshToken refreshToken) {
        return RefreshTokenDTO.builder()
                .id(refreshToken.getId())
                .token(refreshToken.getToken())
                .userId(refreshToken.getUser().getId())
                .expiryDate(refreshToken.getExpiryDate())
                .build();
    }

    public RefreshToken mapToRefreshToken(RefreshTokenDTO refreshTokenDTO) {
        return modelMapper.map(refreshTokenDTO, RefreshToken.class);
    }
}
