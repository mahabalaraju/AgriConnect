package com.agriconnect.marketservice.dto;

import com.agriconnect.marketservice.entity.HarvestRecord.SellStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestRecordResponseDTO {

    private String recordId;
    private String cropId;
    private String farmerId;
    private String cropName;
    private String cropType;
    private Double actualYieldKg;
    private Double expectedYieldKg;
    private Double totalExpenses;
    private Double suggestedPricePerKg;
    private LocalDate harvestDate;
    private SellStatus sellStatus;
    private LocalDateTime createdAt;
}