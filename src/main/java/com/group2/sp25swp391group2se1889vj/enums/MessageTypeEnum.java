package com.group2.sp25swp391group2se1889vj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error");

    private final String type;
}
