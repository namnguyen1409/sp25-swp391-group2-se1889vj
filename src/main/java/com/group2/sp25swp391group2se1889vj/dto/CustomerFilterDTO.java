package com.group2.sp25swp391group2se1889vj.dto;

import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class CustomerFilterDTO {
    @Min(1)
    private int page = 1;
    @Min(1)
    private int size = 10;
    private String orderBy = "createdAt";
    private String direction = "desc";
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime minCreatedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime maxCreatedAt;
}
