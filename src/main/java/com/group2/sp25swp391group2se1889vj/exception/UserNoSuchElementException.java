package com.group2.sp25swp391group2se1889vj.exception;

import lombok.Getter;

@Getter
public class UserNoSuchElementException extends RuntimeException {
    private final Long id;

    public UserNoSuchElementException(String message, Long id) {
        super(message);
        this.id = id;
    }

}
