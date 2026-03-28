package com.agriconnect.marketservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Market Price Not Found
    @ExceptionHandler(MarketPriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMarketPriceNotFoundException(
            MarketPriceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handle Buyer Listing Not Found
    @ExceptionHandler(BuyerListingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBuyerListingNotFoundException(
            BuyerListingNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handle Harvest Record Not Found
    @ExceptionHandler(HarvestRecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHarvestRecordNotFoundException(
            HarvestRecordNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handle Illegal Argument (invalid enum values)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid value: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handle Any Other Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Something went wrong. Please try again.")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
//```
//
//---
//
//**One extra thing market-service has compared to others:**
//
//`IllegalArgumentException` handler — this is new! When someone calls `updateListingStatus` or `updateSellStatus` with an invalid enum value like `"INVALID_STATUS"`, Java throws `IllegalArgumentException`. We catch it here and return a clean `400 BAD_REQUEST` instead of ugly 500 error.
//
//---
//
//**Your exception layer:**
//```
//exception/
//├── MarketPriceNotFoundException.java     ✅
//├── BuyerListingNotFoundException.java    ✅
//├── HarvestRecordNotFoundException.java   ✅
//├── ErrorResponse.java                    ✅
//└── GlobalExceptionHandler.java           ✅
//```
//
//---
//
//Only three things remaining:
//```
//producer/
//├── KafkaConfig.java              ← Kafka setup
//└── MarketEventProducer.java      ← Publishes price.updated
//
//consumer/
//└── CropEventConsumer.java        ← Listens to crop.harvested
//
//controller/
//└── MarketController.java         ← REST APIs