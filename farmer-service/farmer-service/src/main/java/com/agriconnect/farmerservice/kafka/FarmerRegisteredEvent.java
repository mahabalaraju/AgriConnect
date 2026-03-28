package com.agriconnect.farmerservice.kafka;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerRegisteredEvent {

    private String farmerId;
    private String fullName;
    private String phoneNumber;
    private String village;
    private String district;
    private String state;
    private Double totalLandAcres;
    private String preferredLanguage;
    private String registeredAt;
}