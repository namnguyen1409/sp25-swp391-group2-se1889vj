package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtDTO extends BaseDTO {
    @NotNull(message = "Type is required")
    private DebtType type;
    private String description;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime debtAt = LocalDateTime.now();
    private String image;
    private Long customerId;
}
