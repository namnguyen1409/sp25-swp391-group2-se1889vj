package com.group2.sp25swp391group2se1889vj.exception;

public class ZoneNoSuchElementException extends RuntimeException {
    private final Long id;

    public ZoneNoSuchElementException(String message, long id) {
        super(message);
        this.id = id;
    }
}
