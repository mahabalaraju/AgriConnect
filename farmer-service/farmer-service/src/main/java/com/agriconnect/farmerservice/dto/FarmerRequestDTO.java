package com.agriconnect.farmerservice.dto;

import com.agriconnect.farmerservice.entity.Farmer.Language;
import com.agriconnect.farmerservice.entity.FarmLand.IrrigationType;
import com.agriconnect.farmerservice.entity.FarmLand.SoilType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid Indian mobile number")
    private String phoneNumber;

    @NotBlank(message = "Village is required")
    private String village;

    private String taluk;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "State is required")
    private String state;

    @NotNull(message = "Total land acres is required")
    @DecimalMin(value = "0.1", message = "Land must be at least 0.1 acres")
    private Double totalLandAcres;

    private Language preferredLanguage;

    private List<LandRequestDTO> farmLands;
}