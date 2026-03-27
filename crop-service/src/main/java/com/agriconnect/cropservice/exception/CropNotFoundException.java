package com.agriconnect.cropservice.exception;

public class CropNotFoundException extends RuntimeException {

    public CropNotFoundException(String message) {
        super(message);
    }
}