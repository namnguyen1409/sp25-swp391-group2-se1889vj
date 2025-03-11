package com.group2.sp25swp391group2se1889vj.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPackageDTO extends BaseDTO {
    @NotBlank(message = "Tên quy cách không được để trống")
    @Size(min = 1, max = 50, message = "Tên quy cách phải từ 1 đến 50 ký tự")
    private String name;

    @NotNull(message = "khối lượng không được để trống")
    @Min(value = 1, message = "Khối lượng phải lớn hơn 0")
    private Integer weight;

    private Long WarehouseId;

}
