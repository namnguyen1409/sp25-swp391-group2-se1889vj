package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerFilterDTO extends BaseFilterDTO{
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;

}
