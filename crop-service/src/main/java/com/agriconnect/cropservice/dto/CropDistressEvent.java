package com.agriconnect.cropservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropDistressEvent {

    private String cropId;
    private String farmerId;
    private String cropName;
    private String distressReason;
    private String phoneNumber;
    private String village;
    private String district;
    private String reportedAt;
}

//
//**Key things to notice:**
//
//`CropStatusUpdateDTO` — this is a dedicated DTO just for updating crop status. When farmer updates status to `HARVESTED`, they also provide `actualHarvestDate` and `actualYieldKg`. When status is `DISTRESS`, other services react immediately.
//
//**Three separate Kafka event DTOs** — each event carries only the data relevant to it. `CropHarvestedEvent` has yield and expense data because market-service needs it for pricing. `CropDistressEvent` has phone and location because alert-service needs to notify farmer urgently.
//
//`CropExpenseRequestDTO` vs `CropExpenseResponseDTO` — request has no IDs or timestamps, response has everything.
//
//---
//
//**Your DTO layer:**
//```
//dto/
//├── FarmerRegisteredEvent.java    ✅
//├── CropRequestDTO.java           ✅
//├── CropResponseDTO.java          ✅
//├── CropStatusUpdateDTO.java      ✅
//├── CropExpenseRequestDTO.java    ✅
//├── CropExpenseResponseDTO.java   ✅
//├── CropSowedEvent.java           ✅
//├── CropHarvestedEvent.java       ✅
//└── CropDistressEvent.java        ✅