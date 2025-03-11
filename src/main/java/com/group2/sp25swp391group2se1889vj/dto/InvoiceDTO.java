package com.group2.sp25swp391group2se1889vj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO extends BaseDTO {
    private String type;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalPayable;
    private BigDecimal customerBalance;
    private BigDecimal totalPaid;
    private BigDecimal totalDebt;
    private Long customerId;
    private Long warehouseId;
    private Boolean isShipped;
    private String description;
    private List<InvoiceItemDTO> items;
}
