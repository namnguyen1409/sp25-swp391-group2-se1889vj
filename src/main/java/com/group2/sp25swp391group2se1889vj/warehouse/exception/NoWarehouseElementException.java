package com.group2.sp25swp391group2se1889vj.warehouse.exception;

public class NoWarehouseElementException extends RuntimeException {

    private Long id;
    public NoWarehouseElementException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
