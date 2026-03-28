package com.agriconnect.farmerservice.exception;

public class FarmerAlreadyExistsException extends RuntimeException {

    public FarmerAlreadyExistsException(String message) {
        super(message);
    }
}