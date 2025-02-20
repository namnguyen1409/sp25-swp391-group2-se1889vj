package com.group2.sp25swp391group2se1889vj.exception;

import lombok.Getter;

@Getter
public class CustomerNoSuchElementException extends RuntimeException {
    private final Long id;

    public CustomerNoSuchElementException(String message, Long id) {
        super(message);
        this.id = id;
    }

}
