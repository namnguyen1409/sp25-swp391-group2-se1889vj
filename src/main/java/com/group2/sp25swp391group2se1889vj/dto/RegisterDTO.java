package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.validation.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldsEqual(field1 = "password", field2 = "confirmPassword", message = "Mật khẩu không khớp")
public class RegisterDTO {
    @UsernameUnique(message = "Tên đăng nhập đã tồn tại")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 6, max = 50, message = "Tên đăng nhập phải từ 6 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @PasswordStrength(message = "Mật khẩu không đủ mạnh")
    private String password;
    private String confirmPassword;

    @NotBlank(message = "Họ không được để trống")
    @Size(min = 1, max = 50, message = "Họ phải từ 1 đến 50 ký tự")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 1, max = 50, message = "Tên phải từ 1 đến 50 ký tự")
    @Pattern(regexp = "^[^!@#$%^&*()_+=\\[\\]{}|,;:'\"<>?/\\\\~`]*$", message = "Tên không được chứa ký tự đặc biệt")
    private String lastName;

    @Pattern(regexp = "^(\\+84|84|0)(3|5|7|8|9|1[2|6|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ")
    @PhoneUnique(message = "Số điện thoại đã tồn tại")
    private String phone;

    private boolean gender;

    @Past(message = "Ngày sinh phải trước ngày hiện tại")
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Ngày sinh không được để trống")
    @AgeCheck(min = 18, max = 125, message = "Tuổi phải từ 18 đến 125")
    private LocalDate birthday;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 10, max = 255, message = "Địa chỉ phải từ 10 đến 255 ký tự")
    private String address;

    private String recaptchaResponse;
    private String token;


}
