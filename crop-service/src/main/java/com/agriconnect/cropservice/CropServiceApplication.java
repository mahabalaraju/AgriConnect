package com.agriconnect.cropservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableScheduling
@Slf4j
public class CropServiceApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication
                .run(CropServiceApplication.class, args)
                .getEnvironment();

        log.info("\n--------------------------------------------------"
                + "\nCrop Service started successfully!"
                + "\nName        : {}"
                + "\nPort        : {}"
                + "\nConsuming   : farmer.registered topic"
                + "\nPublishing  : crop.sowed, crop.harvested, crop.distress"
                + "\nHealth URL  : http://localhost:{}/actuator/health"
                + "\nAPI BaseURL : http://localhost:{}/api/v1/crops"
                + "\n--------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.port"),
                env.getProperty("server.port")
        );
    }
}
//```
//
//---
//
//**Key things to notice:**
//
//`@PatchMapping` for status update — we use `PATCH` not `PUT` because we're updating only one field (status), not the entire crop object. This is correct REST practice:
//- `PUT` — replace entire resource
//- `PATCH` — update partial resource
//
//`@EnableScheduling` — added for future use. Later we'll add a scheduled job that runs every morning checking `findCropsReadyToHarvest()` and notifying farmers automatically.
//
//`/profit-loss` endpoint — this is your **premium analytics feature**. Farmers can see exactly how much profit or loss they made on each crop. No other platform in Karnataka gives this at field level.
//
//---
//
//**Your complete crop-service:**
//```
//crop-service/
//├── consumer/
//│   └── FarmerEventConsumer.java      ✅
//├── controller/
//│   └── CropController.java           ✅
//├── dto/
//│   ├── FarmerRegisteredEvent.java    ✅
//│   ├── CropRequestDTO.java           ✅
//│   ├── CropResponseDTO.java          ✅
//│   ├── CropStatusUpdateDTO.java      ✅
//│   ├── CropExpenseRequestDTO.java    ✅
//│   ├── CropExpenseResponseDTO.java   ✅
//│   ├── CropSowedEvent.java           ✅
//│   ├── CropHarvestedEvent.java       ✅
//│   └── CropDistressEvent.java        ✅
//├── entity/
//│   ├── Crop.java                     ✅
//│   └── CropExpense.java              ✅
//├── exception/
//│   ├── CropNotFoundException.java    ✅
//│   ├── InvalidStatusTransitionException.java ✅
//│   ├── CropExpenseNotFoundException.java     ✅
//│   ├── ErrorResponse.java            ✅
//│   └── GlobalExceptionHandler.java   ✅
//├── producer/
//│   ├── KafkaConfig.java              ✅
//│   └── CropEventProducer.java        ✅
//├── repository/
//│   ├── CropRepository.java           ✅
//│   └── CropExpenseRepository.java    ✅
//├── service/
//│   └── CropService.java              ✅
//├── serviceimpl/
//│   └── CropServiceImpl.java          ✅
//├── resources/
//│   └── application.properties        ✅
//└── CropServiceApplication.java       ✅
//```
//
//---
//
//**Now run all three services together:**
//```
//docker-compose up -d