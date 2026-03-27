package com.agriconnect.cropservice.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropSowedEvent {

    private String cropId;
    private String farmerId;
    private String cropName;
    private String cropType;
    private LocalDate sowingDate;
    private LocalDate expectedHarvestDate;
    private Double areaInAcres;
    private String village;
    private String district;
    private String sowedAt;
}