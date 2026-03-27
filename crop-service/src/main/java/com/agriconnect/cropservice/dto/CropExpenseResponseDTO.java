package com.agriconnect.cropservice.dto;

import com.agriconnect.cropservice.entity.CropExpense.ExpenseType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropExpenseResponseDTO {

    private String expenseId;
    private ExpenseType expenseType;
    private Double amount;
    private LocalDate expenseDate;
    private String description;
    private LocalDateTime createdAt;
}