package com.agriconnect.cropservice.dto;

import com.agriconnect.cropservice.entity.Crop.CropStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropStatusUpdateDTO {

    @NotNull(message = "Crop status is required")
    private CropStatus cropStatus;

    private LocalDate actualHarvestDate;
    private Double actualYieldKg;
    private String notes;
}