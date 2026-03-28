package com.agriconnect.marketservice;

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
public class MarketServiceApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication
                .run(MarketServiceApplication.class, args)
                .getEnvironment();

        log.info("\n--------------------------------------------------"
                + "\nMarket Service started successfully!"
                + "\nName        : {}"
                + "\nPort        : {}"
                + "\nConsuming   : crop.harvested topic"
                + "\nPublishing  : price.updated topic"
                + "\nHealth URL  : http://localhost:{}/actuator/health"
                + "\nAPI BaseURL : http://localhost:{}/api/v1/market"
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
//`@RequestParam` vs `@PathVariable` — notice how analytics endpoints use `@RequestParam` for filtering:
//```
//GET /analytics/average-price?cropName=Rice&district=Mandya
//```
//While resource endpoints use `@PathVariable`:
//```
//GET /prices/crop/Rice
//```
//This is correct REST design — query parameters for filtering, path variables for identifying specific resources.
//
//`/listings/best-price/{cropName}` — this is your most powerful farmer-facing API. Farmer harvests Rice, calls this endpoint, instantly sees which buyer is offering the highest price. No middleman needed!
//
//`/analytics/suggested-price` — farmer calls this before selling to know what price to demand. Built on 7-day moving average plus 5% margin.
//
//---
//
//**Your complete market-service:**
//```
//market-service/
//├── consumer/
//│   └── CropEventConsumer.java          ✅
//├── controller/
//│   └── MarketController.java           ✅
//├── dto/
//│   ├── CropHarvestedEvent.java         ✅
//│   ├── MarketPriceRequestDTO.java      ✅
//│   ├── MarketPriceResponseDTO.java     ✅
//│   ├── BuyerListingRequestDTO.java     ✅
//│   ├── BuyerListingResponseDTO.java    ✅
//│   ├── HarvestRecordResponseDTO.java   ✅
//│   └── PriceUpdatedEvent.java          ✅
//├── entity/
//│   ├── MarketPrice.java                ✅
//│   ├── BuyerListing.java               ✅
//│   └── HarvestRecord.java              ✅
//├── exception/
//│   ├── MarketPriceNotFoundException    ✅
//│   ├── BuyerListingNotFoundException   ✅
//│   ├── HarvestRecordNotFoundException  ✅
//│   ├── ErrorResponse.java              ✅
//│   └── GlobalExceptionHandler.java     ✅
//├── producer/
//│   ├── KafkaConfig.java                ✅
//│   └── MarketEventProducer.java        ✅
//├── repository/
//│   ├── MarketPriceRepository.java      ✅
//│   ├── BuyerListingRepository.java     ✅
//│   └── HarvestRecordRepository.java    ✅
//├── service/
//│   └── MarketService.java              ✅
//├── serviceimpl/
//│   └── MarketServiceImpl.java          ✅
//├── resources/
//│   └── application.properties          ✅
//└── MarketServiceApplication.java       ✅
//```
//
//---
//
//**Now run all four services together:**
//
//Start in this order:
//```
//1. docker-compose up -d
//2. FarmerServiceApplication    → port 8081
//3. AlertServiceApplication     → port 8084
//4. CropServiceApplication      → port 8082
//5. MarketServiceApplication    → port 8083
//```
//
//**Full end to end test in Postman:**
//```
//Step 1 — Register farmer
//POST http://localhost:8081/api/v1/farmers/register
//
//Step 2 — Add crop
//POST http://localhost:8082/api/v1/crops/add
//
//Step 3 — Add market price
//POST http://localhost:8083/api/v1/market/prices
//
//Step 4 — Update crop status to HARVESTED
//PATCH http://localhost:8082/api/v1/crops/{cropId}/status
//
//Step 5 — Check harvest record
//GET http://localhost:8083/api/v1/market/harvests/crop/{cropId}
//
//Step 6 — Get suggested price
//GET http://localhost:8083/api/v1/market/analytics/suggested-price?cropName=Rice&district=Mandya
//
//Step 7 — Add buyer listing
//POST http://localhost:8083/api/v1/market/listings
//
//Step 8 — Get best price listings
//GET http://localhost:8083/api/v1/market/listings/best-price/Rice