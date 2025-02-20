package com.group2.sp25swp391group2se1889vj.dto;

import lombok.*;


import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO extends BaseDTO {
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime expiryDate;
}