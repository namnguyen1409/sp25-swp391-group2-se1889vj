package com.group2.sp25swp391group2se1889vj.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductSnapshotDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
}
