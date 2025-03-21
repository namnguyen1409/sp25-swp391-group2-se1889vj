package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDataDTO extends BaseDTO {
    private InvoiceType type;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal customerBalance;
    private BigDecimal totalPayable;
    private BigDecimal totalPaid;
    private BigDecimal totalDebt;
    private WarehouseDTO warehouse;
    private CustomerDTO customer;
    private String description;
    private boolean isShipped;
    private boolean isProcessed = false;
    private boolean isSuccess = false;
    private Set<InvoiceItemDTO> items;
    private String createdByUsername;
}
