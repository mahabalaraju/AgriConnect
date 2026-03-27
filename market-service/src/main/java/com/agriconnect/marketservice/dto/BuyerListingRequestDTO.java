package com.agriconnect.marketservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerListingRequestDTO {

    @NotBlank(message = "Buyer name is required")
    private String buyerName;

    @NotBlank(message = "Buyer phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Enter valid Indian mobile number")
    private String buyerPhone;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    private String cropType;

    @NotNull(message = "Quantity required")
    @DecimalMin(value = "1.0", message = "Quantity must be at least 1kg")
    private Double quantityRequiredKg;

    @NotNull(message = "Offered price is required")
    @DecimalMin(value = "0.1", message = "Price must be positive")
    private Double offeredPricePerKg;

    @NotBlank(message = "District is required")
    private String district;

    private String state;

    @NotNull(message = "Valid until date is required")
    private LocalDate validUntil;
}