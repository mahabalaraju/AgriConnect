package com.agriconnect.farmerservice.dto;

import com.agriconnect.farmerservice.entity.FarmLand.IrrigationType;
import com.agriconnect.farmerservice.entity.FarmLand.SoilType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandRequestDTO {

    private String surveyNumber;

    @NotNull(message = "Acres is required")
    @DecimalMin(value = "0.1", message = "Land must be at least 0.1 acres")
    private Double acres;

    private SoilType soilType;

    private IrrigationType irrigationType;

    private Double latitude;

    private Double longitude;
}