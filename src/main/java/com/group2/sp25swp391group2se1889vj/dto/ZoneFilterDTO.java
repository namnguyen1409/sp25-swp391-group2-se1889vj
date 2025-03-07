package com.group2.sp25swp391group2se1889vj.dto;


import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneFilterDTO extends BaseFilterDTO{
    private String name;
    private String productName;
    private Integer minQuantity;
    private Integer maxQuantity;
}
