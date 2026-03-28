package com.agriconnect.farmerservice.exception;

public class FarmerNotFoundException extends RuntimeException {

    public FarmerNotFoundException(String message) {
        super(message);
    }
}