package com.agriconnect.marketservice.dto;

import com.agriconnect.marketservice.entity.MarketPrice.PriceTrend;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPriceRequestDTO {

    @NotBlank(message = "Crop name is required")
    private String cropName;

    private String cropType;

    @NotBlank(message = "Mandi name is required")
    private String mandiName;

    @NotBlank(message = "District is required")
    private String district;

    private String state;

    @NotNull(message = "Min price is required")
    @DecimalMin(value = "0.1", message = "Price must be positive")
    private Double minPricePerKg;

    @NotNull(message = "Max price is required")
    @DecimalMin(value = "0.1", message = "Price must be positive")
    private Double maxPricePerKg;

    @NotNull(message = "Modal price is required")
    @DecimalMin(value = "0.1", message = "Price must be positive")
    private Double modalPricePerKg;

    @NotNull(message = "Price date is required")
    private LocalDate priceDate;

    private PriceTrend priceTrend;
}