package com.agriconnect.farmerservice.kafka;


	import com.agriconnect.farmerservice.entity.Farmer;
	import lombok.RequiredArgsConstructor;
	import lombok.extern.slf4j.Slf4j;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.kafka.core.KafkaTemplate;
	import org.springframework.kafka.support.SendResult;
	import org.springframework.stereotype.Component;
	import java.util.concurrent.CompletableFuture;

	@Component
	@RequiredArgsConstructor
	@Slf4j
	public class FarmerEventProducer {

	    private final KafkaTemplate<String, Object> kafkaTemplate;

	    @Value("${kafka.topic.farmer-registered}")
	    private String farmerRegisteredTopic;

	    // ─────────────────────────────────────────
	    // Publish Farmer Registered Event
	    // ─────────────────────────────────────────
	    public void publishFarmerRegistered(Farmer farmer) {
	        FarmerRegisteredEvent event = FarmerRegisteredEvent.builder()
	                .farmerId(farmer.getFarmerId())
	                .fullName(farmer.getFullName())
	                .phoneNumber(farmer.getPhoneNumber())
	                .village(farmer.getVillage())
	                .district(farmer.getDistrict())
	                .state(farmer.getState())
	                .totalLandAcres(farmer.getTotalLandAcres())
	                .preferredLanguage(farmer.getPreferredLanguage().name())
	                .registeredAt(farmer.getCreatedAt().toString())
	                .build();

	        log.info("Publishing farmer.registered event for farmerID: {}", 
	                farmer.getFarmerId());

	        CompletableFuture<SendResult<String, Object>> future =
	                kafkaTemplate.send(farmerRegisteredTopic, farmer.getFarmerId(), event);

	        future.whenComplete((result, ex) -> {
	            if (ex != null) {
	                log.error("Failed to publish farmer.registered event for farmerID: {} | Error: {}",
	                        farmer.getFarmerId(), ex.getMessage());
	            } else {
	                log.info("Successfully published farmer.registered event | " +
	                         "farmerID: {} | Topic: {} | Partition: {} | Offset: {}",
	                        farmer.getFarmerId(),
	                        result.getRecordMetadata().topic(),
	                        result.getRecordMetadata().partition(),
	                        result.getRecordMetadata().offset());
	            }
	        });
	    }
	}
