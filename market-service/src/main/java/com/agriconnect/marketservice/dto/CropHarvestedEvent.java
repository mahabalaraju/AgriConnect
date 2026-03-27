package com.agriconnect.marketservice.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropHarvestedEvent {

    private String cropId;
    private String farmerId;
    private String cropName;
    private String cropType;
    private LocalDate actualHarvestDate;
    private Double actualYieldKg;
    private Double expectedYieldKg;
    private Double totalExpenses;
    private String harvestedAt;
}