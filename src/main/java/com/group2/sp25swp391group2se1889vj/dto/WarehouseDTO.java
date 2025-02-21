package com.group2.sp25swp391group2se1889vj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseDTO extends BaseDTO{
    @NotBlank(message = "Tên kho không được để trống")
    @Size(min = 6, max = 50, message = "Tên kho phải có từ 6 đến 50 ký tự")
    @Pattern(regexp = "^[^!@#$%^&*()_+=\\[\\]{}|,;:'\"<>?/\\\\~`]*$", message = "Tên kho không được chứa ký tự đặc biệt")
    private String name;
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 10, max = 255, message = "Địa chỉ phải từ 10 đến 255 ký tự")
    @Pattern(regexp = "^[^!@#$%^&*()_+=\\[\\]{}|,;:'\"<>?/\\\\~`]*$", message = "Địa chỉ không được chứa ký tự đặc biệt")
    private String location;
    private String description;
}
