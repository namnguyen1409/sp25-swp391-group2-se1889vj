package com.group2.sp25swp391group2se1889vj.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO extends BaseDTO {
    @NotBlank(message = "Tên gạo không được để trống")
    @Size(min = 3, max = 50, message = "Tên gạo phải có từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[^!@#$%^&*()_+=\\[\\]{}|,;:'\"<>?/\\\\~`]*$", message = "Tên kho gạo được chứa ký tự đặc biệt")
    private String name;
    @NotNull(message = "Giá không được để trống")
    private BigDecimal price;
    @NotBlank(message = "Mô tả không được để trống")
    private String description;
    @NotBlank(message = "Hình ảnh không được để trống")
    private String image;
    private Long productPackageId;
    private String descriptionPlainText;
    private int stockQuantity;
    private Long warehouseId;
    private Set<Long> zoneIds = new HashSet<>();
}
