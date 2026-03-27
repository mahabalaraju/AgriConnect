package com.agriconnect.alertservice.consumer;

import com.agriconnect.alertservice.dto.FarmerRegisteredEvent;
import com.agriconnect.alertservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import org.springframework.retry.annotation.Backoff;

@Component
@RequiredArgsConstructor
@Slf4j
public class FarmerEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "farmer.registered",
        groupId = "alert-service-group"
    )
    @RetryableTopic(
    	    attempts = "3",
    	    backoff = @Backoff(delay = 1000, multiplier = 2.0),
    	    dltTopicSuffix = ".DLT"
    	)
    public void consumeFarmerRegistered(
            ConsumerRecord<String, FarmerRegisteredEvent> record,
            Acknowledgment acknowledgment) {

        log.info("Received farmer.registered event | " +
                 "farmerID: {} | Partition: {} | Offset: {}",
                record.key(),
                record.partition(),
                record.offset());

        try {
            FarmerRegisteredEvent event = record.value();

            // Validate event
            if (event == null || event.getFarmerId() == null) {
                log.error("Invalid event received - skipping");
                acknowledgment.acknowledge();
                return;
            }

            // Process notification
            notificationService.sendWelcomeNotification(event);

            // Manually acknowledge - tells Kafka message processed successfully
            acknowledgment.acknowledge();

            log.info("farmer.registered event processed successfully " +
                     "for farmerID: {}", event.getFarmerId());

        } catch (Exception e) {
            log.error("Error processing farmer.registered event " +
                      "for farmerID: {} | Error: {}",
                    record.key(), e.getMessage());
            // Don't acknowledge - Kafka will retry
            throw e;
        }
    }
}