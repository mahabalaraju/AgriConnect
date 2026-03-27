package com.agriconnect.marketservice.dto;

import com.agriconnect.marketservice.entity.BuyerListing.ListingStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerListingResponseDTO {

    private String listingId;
    private String buyerName;
    private String buyerPhone;
    private String cropName;
    private String cropType;
    private Double quantityRequiredKg;
    private Double offeredPricePerKg;
    private String district;
    private String state;
    private LocalDate validUntil;
    private ListingStatus listingStatus;
    private LocalDateTime createdAt;
}