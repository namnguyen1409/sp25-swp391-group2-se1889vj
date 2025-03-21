package com.group2.sp25swp391group2se1889vj.dto;


import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneFilterDTO extends BaseFilterDTO {
    private String name;
    private String productName;
    private LocalDate minUpdateAt;
    private LocalDate maxUpdateAt;
}
