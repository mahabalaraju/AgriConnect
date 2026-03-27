package com.agriconnect.cropservice.producer;

import com.agriconnect.cropservice.dto.CropDistressEvent;
import com.agriconnect.cropservice.dto.CropHarvestedEvent;
import com.agriconnect.cropservice.dto.CropSowedEvent;
import com.agriconnect.cropservice.entity.Crop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class CropEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.crop-sowed}")
    private String cropSowedTopic;

    @Value("${kafka.topic.crop-harvested}")
    private String cropHarvestedTopic;

    @Value("${kafka.topic.crop-distress}")
    private String cropDistressTopic;

    // Publish crop.sowed event
    public void publishCropSowed(Crop crop) {
        CropSowedEvent event = CropSowedEvent.builder()
                .cropId(crop.getCropId())
                .farmerId(crop.getFarmerId())
                .cropName(crop.getCropName())
                .cropType(crop.getCropType().name())
                .sowingDate(crop.getSowingDate())
                .expectedHarvestDate(crop.getExpectedHarvestDate())
                .areaInAcres(crop.getAreaInAcres())
                .sowedAt(LocalDateTime.now().toString())
                .build();

        sendEvent(cropSowedTopic, crop.getFarmerId(), event, "crop.sowed");
    }

    // Publish crop.harvested event
    public void publishCropHarvested(Crop crop, Double totalExpenses) {
        CropHarvestedEvent event = CropHarvestedEvent.builder()
                .cropId(crop.getCropId())
                .farmerId(crop.getFarmerId())
                .cropName(crop.getCropName())
                .cropType(crop.getCropType().name())
                .actualHarvestDate(crop.getActualHarvestDate())
                .actualYieldKg(crop.getActualYieldKg())
                .expectedYieldKg(crop.getExpectedYieldKg())
                .totalExpenses(totalExpenses)
                .harvestedAt(LocalDateTime.now().toString())
                .build();

        sendEvent(cropHarvestedTopic, crop.getFarmerId(), event, 
                "crop.harvested");
    }

    // Publish crop.distress event
    public void publishCropDistress(Crop crop, String distressReason) {
        CropDistressEvent event = CropDistressEvent.builder()
                .cropId(crop.getCropId())
                .farmerId(crop.getFarmerId())
                .cropName(crop.getCropName())
                .distressReason(distressReason)
                .reportedAt(LocalDateTime.now().toString())
                .build();

        sendEvent(cropDistressTopic, crop.getFarmerId(), event, 
                "crop.distress");
    }

    // Common send method
    private void sendEvent(String topic, String key, 
            Object event, String eventName) {
        log.info("Publishing {} event for farmerID: {}", eventName, key);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish {} event | Error: {}",
                        eventName, ex.getMessage());
            } else {
                log.info("Successfully published {} event | " +
                         "Topic: {} | Partition: {} | Offset: {}",
                        eventName,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
//```
//
//---
//
//**Key things to notice:**
//
//**Common `sendEvent` method** — instead of repeating the same `CompletableFuture` code 3 times, we extracted it into one reusable method. This is **DRY principle** — Don't Repeat Yourself. Clean professional code.
//
//**Three separate topics** — `crop.sowed`, `crop.harvested`, `crop.distress` each serve different consumers. alert-service listens to distress, market-service listens to harvested. Each service only subscribes to what it needs.
//
//**`farmerId` as Kafka message key** — all crop events for the same farmer go to same partition. This ensures ordering — if a farmer sows and then harvests, those events are always processed in order.
//
//---
//
//**Your producer package:**
//```
//producer/
//├── KafkaConfig.java          ✅
//└── CropEventProducer.java    ✅