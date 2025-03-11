package com.group2.sp25swp391group2se1889vj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneDTO extends BaseDTO {
    @NotBlank(message = "Tên khu vực không được để trống")
    @Size(min = 6, max = 50, message = "Tên khu vực phải có từ 6 đến 50 ký tự")
    @Pattern(regexp = "^[^!@#$%^&*()_+=\\[\\]{}|,;:'\"<>?/\\\\~`]*$", message = "Tên khu vực không được chứa ký tự đặc biệt")
    private String name;
    private String description;
    private Long warehouseId;
    private Long productId;
    private String productName;
    private String productImage;
}
