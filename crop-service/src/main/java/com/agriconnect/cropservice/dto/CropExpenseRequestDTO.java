package com.agriconnect.cropservice.dto;

import com.agriconnect.cropservice.entity.CropExpense.ExpenseType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropExpenseRequestDTO {

    @NotNull(message = "Expense type is required")
    private ExpenseType expenseType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private Double amount;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private String description;
}