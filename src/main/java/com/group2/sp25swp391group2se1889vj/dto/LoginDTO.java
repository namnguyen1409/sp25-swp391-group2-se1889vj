package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.validation.annotation.UsernameExist;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @UsernameExist()
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    private Boolean remember;
    private String recaptchaResponse;
}
