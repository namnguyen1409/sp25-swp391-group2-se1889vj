package com.group2.sp25swp391group2se1889vj.exception;

import lombok.Getter;

@Getter
public class ProductNoSuchElementException extends RuntimeException {
    private final Long id;

    public ProductNoSuchElementException(String message, Long id) {
        super(message);
        this.id = id;
    }

}
