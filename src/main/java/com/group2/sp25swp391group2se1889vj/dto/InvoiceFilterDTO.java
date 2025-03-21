package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceFilterDTO extends BaseFilterDTO {
    private InvoiceType type;

    private BigDecimal totalPriceFrom;
    private BigDecimal totalPriceTo;

    private BigDecimal totalDiscountFrom;
    private BigDecimal totalDiscountTo;

    private BigDecimal customerBalanceFrom;
    private BigDecimal customerBalanceTo;

    private BigDecimal totalPayableFrom;
    private BigDecimal totalPayableTo;

    private BigDecimal totalPaidFrom;
    private BigDecimal totalPaidTo;

    private BigDecimal totalDebtFrom;
    private BigDecimal totalDebtTo;

        private String customerFullName;
    private String customerPhone;

    private String description;

    private String createdByUsername;

}
