package com.group2.sp25swp391group2se1889vj.warehouse.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoZoneElementException extends RuntimeException {
    private Long id;

    public NoZoneElementException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
