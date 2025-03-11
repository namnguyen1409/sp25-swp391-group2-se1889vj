package com.group2.sp25swp391group2se1889vj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDTO extends BaseDTO{
    private Long productId;
    private Long productPackageId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal payable;
}
