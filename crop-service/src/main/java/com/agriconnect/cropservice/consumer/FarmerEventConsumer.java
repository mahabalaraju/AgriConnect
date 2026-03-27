package com.agriconnect.cropservice.consumer;

import com.agriconnect.cropservice.dto.FarmerRegisteredEvent;
import com.agriconnect.cropservice.entity.Crop;
import com.agriconnect.cropservice.entity.Crop.CropType;
import com.agriconnect.cropservice.repository.CropRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class FarmerEventConsumer {

    private final CropRepository cropRepository;

    @KafkaListener(
        topics = "farmer.registered",
        groupId = "crop-service-group"
    )
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2),
        dltTopicSuffix = ".DLT"
    )
    public void consumeFarmerRegistered(
            ConsumerRecord<String, FarmerRegisteredEvent> record,
            Acknowledgment acknowledgment) {

        log.info("Received farmer.registered event in crop-service | " +
                 "farmerID: {} | Partition: {} | Offset: {}",
                record.key(),
                record.partition(),
                record.offset());

        try {
            FarmerRegisteredEvent event = record.value();

            // Validate event
            if (event == null || event.getFarmerId() == null) {
                log.error("Invalid farmer.registered event - skipping");
                acknowledgment.acknowledge();
                return;
            }

            // Create a default crop profile for new farmer
            createDefaultCropProfile(event);

            // Acknowledge message
            acknowledgment.acknowledge();

            log.info("farmer.registered event processed successfully " +
                     "in crop-service for farmerID: {}", 
                     event.getFarmerId());

        } catch (Exception e) {
            log.error("Error processing farmer.registered event | " +
                      "farmerID: {} | Error: {}",
                    record.key(), e.getMessage());
            throw e;
        }
    }

    // ─────────────────────────────────────────
    // Create default crop profile for new farmer
    // ─────────────────────────────────────────
    private void createDefaultCropProfile(FarmerRegisteredEvent event) {
        log.info("Creating default crop profile for farmerID: {}",
                event.getFarmerId());

        // Check if farmer already has crops
        long existingCrops = cropRepository.countByFarmerId(
                event.getFarmerId());

        if (existingCrops > 0) {
            log.info("Farmer already has crops - skipping default profile");
            return;
        }

        // Create a default planned crop based on district
        // Karnataka farmers typically grow Ragi, Rice, or Maize
        String defaultCropName = getDefaultCropByDistrict(event.getDistrict());

        Crop defaultCrop = Crop.builder()
                .farmerId(event.getFarmerId())
                .cropName(defaultCropName)
                .cropType(CropType.CEREAL)
                .sowingDate(LocalDate.now())
                .expectedHarvestDate(LocalDate.now().plusMonths(4))
                .areaInAcres(event.getTotalLandAcres())
                .expectedYieldKg(event.getTotalLandAcres() * 800)
                .notes("Default crop profile created on registration")
                .build();

        cropRepository.save(defaultCrop);

        log.info("Default crop profile created | cropName: {} | " +
                 "farmerID: {}",
                defaultCropName, event.getFarmerId());
    }

    // ─────────────────────────────────────────
    // Get default crop based on Karnataka district
    // ─────────────────────────────────────────
    private String getDefaultCropByDistrict(String district) {
        if (district == null) return "Ragi";

        return switch (district.toLowerCase()) {
            case "chikkamagaluru",
                 "hassan",
                 "kodagu"      -> "Coffee";
            case "mysuru",
                 "mandya",
                 "raichur"     -> "Rice";
            case "dharwad",
                 "gadag",
                 "haveri"      -> "Cotton";
            case "kolar",
                 "chikkaballapur" -> "Tomato";
            case "bagalkote",
                 "vijayapura"  -> "Sugarcane";
            default            -> "Ragi";
        };
    }
}
//**Key things to notice:**
//
//`groupId = "crop-service-group"` — different from alert-service's `alert-service-group`. Both services consume the same `farmer.registered` event independently. Kafka delivers the message to **both groups**. This is the power of Kafka pub-sub model.
//
//`getDefaultCropByDistrict` — this is hyperlocal intelligence. Chikkamagaluru farmers get Coffee as default, Mandya farmers get Rice, Dharwad farmers get Cotton. This is exactly the kind of **domain knowledge** that makes AgriConnect special compared to generic platforms.
//
//`expectedYieldKg = totalLandAcres * 800` — rough estimate of 800kg per acre. Real world you'd have crop-specific yield data.
//
//`countByFarmerId` check — prevents duplicate default profiles if the event is processed twice due to retry. This is **idempotency** — processing same message multiple times gives same result.
//
//---
//
//**Full Kafka flow now:**
//```
//farmer-service                    
//    │                             
//    │ farmer.registered           
//    ├──────────────────▶ alert-service-group
//    │                       │
//    │                       └─ sends welcome SMS
//    │
//    └──────────────────▶ crop-service-group
//                            │
//                            └─ creates default crop profile
//                            └─ publishes crop.sowed
//                                    │
//                                    └─▶ alert-service (coming)
//                                    └─▶ market-service (coming)
//```
//
//---
//
//**Your consumer package:**
//```
//consumer/
//└── FarmerEventConsumer.java    ✅