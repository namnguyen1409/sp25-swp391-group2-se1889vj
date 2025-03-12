package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailDTO extends BaseDTO{
    private InvoiceType type;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalPayable;
    private BigDecimal customerBalance;
    private BigDecimal totalPaid;
    private BigDecimal totalDebt;
    private String customerFullName;
    private String customerPhone;
    private String description;
    private boolean isShipped;
    private boolean isProcessed;
    private boolean isSuccess;
}
