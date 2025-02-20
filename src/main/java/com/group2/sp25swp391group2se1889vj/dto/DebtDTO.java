package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtDTO extends BaseDTO {
    private DebtType type;
    private String description;
    private BigDecimal amount;
    private LocalDateTime debtAt;
    private String image;
    private Long customerId;
}
