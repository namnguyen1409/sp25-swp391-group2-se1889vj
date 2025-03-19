package com.group2.sp25swp391group2se1889vj.dto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDTO extends BaseFilterDTO{
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime minCreatedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime maxCreatedAt;
}
