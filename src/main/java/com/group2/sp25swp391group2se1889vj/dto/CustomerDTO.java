package com.group2.sp25swp391group2se1889vj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO extends BaseDTO {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 6, max = 50, message = "Họ và tên phải từ 6 đến 100 ký tự")
    private String fullName;
    @Pattern(regexp = "^(\\+84|84|0)(3|5|7|8|9|1[2|6|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    private String email;
    private String address;
    private Long warehouseId;
    private BigDecimal balance = BigDecimal.ZERO;
}
