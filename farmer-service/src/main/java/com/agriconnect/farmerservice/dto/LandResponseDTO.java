package com.agriconnect.farmerservice.dto;

import com.agriconnect.farmerservice.entity.FarmLand.IrrigationType;
import com.agriconnect.farmerservice.entity.FarmLand.SoilType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandResponseDTO {

    private String landId;
    private String surveyNumber;
    private Double acres;
    private SoilType soilType;
    private IrrigationType irrigationType;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
}