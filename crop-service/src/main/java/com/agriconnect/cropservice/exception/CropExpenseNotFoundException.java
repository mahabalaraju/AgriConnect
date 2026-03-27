package com.agriconnect.cropservice.exception;

public class CropExpenseNotFoundException extends RuntimeException {

    public CropExpenseNotFoundException(String message) {
        super(message);
    }
}