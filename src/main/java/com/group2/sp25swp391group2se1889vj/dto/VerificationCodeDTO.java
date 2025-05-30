package com.group2.sp25swp391group2se1889vj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodeDTO {
    private Long id;
    private String email;
    private String code;
}
