package com.group2.sp25swp391group2se1889vj.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceStatusMessage {
    private Long orderId;
    private String status;
    private String message;
    private String createdBy;
}
