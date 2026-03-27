package com.agriconnect.cropservice.dto;

import com.agriconnect.cropservice.entity.Crop.CropStatus;
import com.agriconnect.cropservice.entity.Crop.CropType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropResponseDTO {

    private String cropId;
    private String farmerId;
    private String landId;
    private String cropName;
    private CropType cropType;
    private CropStatus cropStatus;
    private LocalDate sowingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;
    private Double areaInAcres;
    private Double expectedYieldKg;
    private Double actualYieldKg;
    private String notes;
    private LocalDateTime createdAt;
    private List<CropExpenseResponseDTO> expenses;
}