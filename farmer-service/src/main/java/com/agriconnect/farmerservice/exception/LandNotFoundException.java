package com.agriconnect.farmerservice.exception;

public class LandNotFoundException extends RuntimeException {

    public LandNotFoundException(String message) {
        super(message);
    }
}