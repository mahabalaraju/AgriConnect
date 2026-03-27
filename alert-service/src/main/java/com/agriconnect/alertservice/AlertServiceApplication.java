package com.agriconnect.alertservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@Slf4j
public class AlertServiceApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication
                .run(AlertServiceApplication.class, args)
                .getEnvironment();

        log.info("\n--------------------------------------------------"
                + "\nAlert Service started successfully!"
                + "\nName        : {}"
                + "\nPort        : {}"
                + "\nListening   : farmer.registered topic"
                + "\nHealth URL  : http://localhost:{}/actuator/health"
                + "\n--------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.port")
        );
    }
}


//**Key things to notice and learn:**
//
//`@KafkaListener` — this is the magic annotation. Spring automatically starts listening to `farmer.registered` topic as soon as service starts.
//
//`@RetryableTopic(attempts = "3")` — if processing fails, Kafka automatically retries 3 times with increasing delay (1s, 2s, 4s). This is called **exponential backoff**.
//
//`dltTopicSuffix = ".DLT"` — if all 3 retries fail, message goes to `farmer.registered.DLT` (Dead Letter Topic). You can investigate failed messages there later. This is production-grade error handling.
//
//`Acknowledgment.acknowledge()` — we manually tell Kafka "I have successfully processed this message, move to next one." If we don't acknowledge, Kafka will redeliver the message.
//
//`ConsumerRecord<String, FarmerRegisteredEvent>` — gives you full message details including partition and offset, not just the value.
//
//---
//
//**Your alert-service structure is complete:**
//```
//alert-service/
//├── consumer/
//│   └── FarmerEventConsumer.java      ✅
//├── dto/
//│   └── FarmerRegisteredEvent.java    ✅
//├── service/
//│   └── NotificationService.java      ✅
//├── serviceimpl/
//│   └── NotificationServiceImpl.java  ✅
//├── resources/
//│   └── application.properties        ✅
//└── AlertServiceApplication.java      ✅
//```
//
//---
//
//**Now run both services together:**
//
//Make sure Docker is running, then start both:
//1. `FarmerServiceApplication` — Run As Spring Boot App
//2. `AlertServiceApplication` — Run As Spring Boot App
//
//Then hit the register farmer API in Postman. You should see in **alert-service console:**
//```
//Received farmer.registered event | farmerID: xxx
//SMS sent to: 9876543210 | Message: Welcome to AgriConnect, Raju Kumar!...
//Push notification sent to farmerID: xxx
//farmer.registered event processed successfully