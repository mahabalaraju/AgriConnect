package com.agriconnect.farmerservice.dto;

import com.agriconnect.farmerservice.entity.Farmer.Language;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerResponseDTO {

    private String farmerId;
    private String fullName;
    private String phoneNumber;
    private String village;
    private String taluk;
    private String district;
    private String state;
    private Double totalLandAcres;
    private Language preferredLanguage;
    private LocalDateTime createdAt;
    private List<LandResponseDTO> farmLands;
}