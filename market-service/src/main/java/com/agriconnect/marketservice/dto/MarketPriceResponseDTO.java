package com.agriconnect.marketservice.dto;

import com.agriconnect.marketservice.entity.MarketPrice.PriceTrend;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPriceResponseDTO {

    private String priceId;
    private String cropName;
    private String cropType;
    private String mandiName;
    private String district;
    private String state;
    private Double minPricePerKg;
    private Double maxPricePerKg;
    private Double modalPricePerKg;
    private LocalDate priceDate;
    private PriceTrend priceTrend;
    private LocalDateTime createdAt;
}