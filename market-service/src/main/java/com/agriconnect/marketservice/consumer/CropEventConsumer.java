package com.agriconnect.marketservice.consumer;

import com.agriconnect.marketservice.dto.CropHarvestedEvent;
import com.agriconnect.marketservice.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CropEventConsumer {

    private final MarketService marketService;

    @KafkaListener(
        topics = "crop.harvested",
        groupId = "market-service-group"
    )
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2),
        dltTopicSuffix = ".DLT"
    )
    public void consumeCropHarvested(
            ConsumerRecord<String, CropHarvestedEvent> record,
            Acknowledgment acknowledgment) {

        log.info("Received crop.harvested event in market-service | " +
                 "farmerID: {} | Partition: {} | Offset: {}",
                record.key(),
                record.partition(),
                record.offset());

        try {
            CropHarvestedEvent event = record.value();

            // Validate event
            if (event == null || event.getCropId() == null) {
                log.error("Invalid crop.harvested event - skipping");
                acknowledgment.acknowledge();
                return;
            }

            log.info("Processing harvest | cropID: {} | crop: {} | " +
                     "yield: {}kg | expenses: Rs.{}",
                    event.getCropId(),
                    event.getCropName(),
                    event.getActualYieldKg(),
                    event.getTotalExpenses());

            // Process harvest event
            marketService.processHarvestEvent(event);

            // Acknowledge message
            acknowledgment.acknowledge();

            log.info("crop.harvested event processed successfully | " +
                     "cropID: {}", event.getCropId());

        } catch (Exception e) {
            log.error("Error processing crop.harvested event | " +
                      "farmerID: {} | Error: {}",
                    record.key(), e.getMessage());
            throw e;
        }
    }
}
//```
//
//---
//
//**Key things to notice:**
//
//`groupId = "market-service-group"` — completely independent from alert-service and crop-service consumer groups. All three services receive `crop.harvested` independently if needed in future.
//
//**Delegation to service layer** — consumer only handles Kafka concerns (receiving, validating, acknowledging). All business logic is in `MarketServiceImpl.processHarvestEvent()`. This keeps consumer clean and testable.
//
//**Null check before processing** — if event or cropId is null, we acknowledge and skip. No point retrying a corrupt message.
//
//**Don't acknowledge on exception** — if processing fails, we throw the exception. `@RetryableTopic` catches it and retries 3 times. If all retries fail, message goes to `crop.harvested.DLT` for investigation.
//
//---
//
//**Now your complete Kafka topic map:**
//```
//Topics created:
//farmer.registered     ← farmer-service produces
//crop.sowed            ← crop-service produces
//crop.harvested        ← crop-service produces
//crop.distress         ← crop-service produces
//price.updated         ← market-service produces
//
//Dead Letter Topics (auto created):
//farmer.registered.DLT
//crop.harvested.DLT
//```
//
//---
//
//**Your consumer package:**
//```
//consumer/
//└── CropEventConsumer.java    ✅
//```
//
//Only two things remaining:
//```
//controller/
//└── MarketController.java     ← REST APIs
//
//MarketServiceApplication.java ← Main class