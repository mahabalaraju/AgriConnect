package com.agriconnect.cropservice.exception;

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

    // Handle Crop Not Found
    @ExceptionHandler(CropNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCropNotFoundException(
            CropNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handle Invalid Status Transition
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransition(
            InvalidStatusTransitionException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handle Crop Expense Not Found
    @ExceptionHandler(CropExpenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCropExpenseNotFoundException(
            CropExpenseNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
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
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

//
//**Key things to notice:**
//
//`InvalidStatusTransitionException` returns `400 BAD_REQUEST` — because it's the client's fault for sending wrong status transition. For example trying to move crop from `HARVESTED` to `GROWING` is a bad request.
//
//**Same pattern as farmer-service** — this is intentional. Consistent exception handling across all microservices makes the system predictable. Any frontend developer knows exactly what error format to expect.
//
//---
//
//**Your exception layer:**
//```
//exception/
//├── CropNotFoundException.java            ✅
//├── InvalidStatusTransitionException.java ✅
//├── CropExpenseNotFoundException.java     ✅
//├── ErrorResponse.java                    ✅
//└── GlobalExceptionHandler.java           ✅
//```
//
//---
//
//Only three things remaining:
//```
//producer/
//└── CropEventProducer.java    ← Kafka producer
//
//consumer/
//└── FarmerEventConsumer.java  ← Kafka consumer
//
//controller/
//└── CropController.java       ← REST APIs