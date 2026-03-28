package com.agriconnect.marketservice.producer;

import com.agriconnect.marketservice.dto.PriceUpdatedEvent;
import com.agriconnect.marketservice.entity.MarketPrice;
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
public class MarketEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.price-updated}")
    private String priceUpdatedTopic;

    public void publishPriceUpdated(MarketPrice price) {
        PriceUpdatedEvent event = PriceUpdatedEvent.builder()
                .priceId(price.getPriceId())
                .cropName(price.getCropName())
                .district(price.getDistrict())
                .state(price.getState())
                .modalPricePerKg(price.getModalPricePerKg())
                .priceTrend(price.getPriceTrend() != null ?
                        price.getPriceTrend().name() : "STABLE")
                .priceDate(price.getPriceDate())
                .updatedAt(LocalDateTime.now().toString())
                .build();

        log.info("Publishing price.updated event | crop: {} | " +
                 "district: {} | price: Rs.{}/kg | trend: {}",
                price.getCropName(),
                price.getDistrict(),
                price.getModalPricePerKg(),
                price.getPriceTrend());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(
                        priceUpdatedTopic,
                        price.getDistrict(),
                        event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish price.updated event | " +
                          "crop: {} | Error: {}",
                        price.getCropName(), ex.getMessage());
            } else {
                log.info("Successfully published price.updated event | " +
                         "Topic: {} | Partition: {} | Offset: {}",
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
//**`district` as Kafka message key** — this is a smart design decision. All price updates for the same district go to the same partition. When alert-service consumes, it can process all Chikkamagaluru price updates in order. Farmers in same district always get price updates in correct sequence.
//
//**Only one topic** `price.updated` — market-service only publishes one event type. Simple and focused. alert-service will consume this and notify relevant farmers.
//
//---
//
//**Complete Kafka flow across all services now:**
//```
//farmer-service
//    │
//    │ farmer.registered
//    ├──────────────▶ alert-service  (welcome SMS)
//    └──────────────▶ crop-service   (default crop profile)
//                         │
//                         │ crop.sowed
//                         ├──────────────▶ alert-service (sowing alert)
//                         │
//                         │ crop.harvested
//                         ├──────────────▶ market-service (harvest record)
//                         │
//                         │ crop.distress
//                         └──────────────▶ alert-service (urgent alert)
//
//market-service
//    │
//    │ price.updated
//    └──────────────▶ alert-service  (price notification to farmers)
//```
//
//---
//
//**Your producer package:**
//```
//producer/
//├── KafkaConfig.java            ✅
//└── MarketEventProducer.java    ✅