package com.agriconnect.farmerservice.exception;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
}