package com.group2.sp25swp391group2se1889vj.user.dto;

import com.group2.sp25swp391group2se1889vj.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime expiryDate;
}
