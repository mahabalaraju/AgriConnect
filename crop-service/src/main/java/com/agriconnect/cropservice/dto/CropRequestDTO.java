package com.agriconnect.cropservice.dto;

import com.agriconnect.cropservice.entity.Crop.CropType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropRequestDTO {

    @NotBlank(message = "Farmer ID is required")
    private String farmerId;

    private String landId;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotNull(message = "Crop type is required")
    private CropType cropType;

    @NotNull(message = "Sowing date is required")
    private LocalDate sowingDate;

    @NotNull(message = "Expected harvest date is required")
    private LocalDate expectedHarvestDate;

    @NotNull(message = "Area in acres is required")
    @DecimalMin(value = "0.1", message = "Area must be at least 0.1 acres")
    private Double areaInAcres;

    @DecimalMin(value = "0.1", message = "Expected yield must be positive")
    private Double expectedYieldKg;

    private String notes;
}