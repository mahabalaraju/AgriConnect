package com.agriconnect.marketservice.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceUpdatedEvent {

    private String priceId;
    private String cropName;
    private String district;
    private String state;
    private Double modalPricePerKg;
    private String priceTrend;
    private LocalDate priceDate;
    private String updatedAt;
}
//```
//
//---
//
//**Key things to notice:**
//
//`HarvestRecordResponseDTO` has `suggestedPricePerKg` — this is automatically calculated when market-service receives `crop.harvested` event. It looks up current mandi price for that crop in that district and suggests the best selling price to farmer. Pure value!
//
//`PriceUpdatedEvent` — when admin updates mandi prices, this event is published. alert-service listens and notifies relevant farmers. For example Rice price RISING in Mandya — all Rice farmers in Mandya get SMS instantly.
//
//`BuyerListingRequestDTO` has `validUntil` — buyer listings expire automatically. Tomorrow we can add a scheduled job that marks expired listings as `EXPIRED` status automatically.
//
//---
//
//**Your DTO layer:**
//```
//dto/
//├── CropHarvestedEvent.java         ✅
//├── MarketPriceRequestDTO.java      ✅
//├── MarketPriceResponseDTO.java     ✅
//├── BuyerListingRequestDTO.java     ✅
//├── BuyerListingResponseDTO.java    ✅
//├── HarvestRecordResponseDTO.java   ✅
//└── PriceUpdatedEvent.java          ✅