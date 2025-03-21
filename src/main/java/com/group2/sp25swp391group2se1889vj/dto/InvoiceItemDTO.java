package com.group2.sp25swp391group2se1889vj.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.sp25swp391group2se1889vj.exception.Http500;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDTO extends BaseDTO{
    private Long productId;
    private Long productPackageId;
    private ProductPackageDTO productPackage;
    private ProductDTO product;
    private Integer quantity;
    private Integer weight;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal payable;

}
