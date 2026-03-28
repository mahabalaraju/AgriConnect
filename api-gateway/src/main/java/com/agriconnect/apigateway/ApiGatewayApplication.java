package com.agriconnect.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class ApiGatewayApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication
                .run(ApiGatewayApplication.class, args)
                .getEnvironment();

        log.info("\n--------------------------------------------------"
                + "\nAPI Gateway started successfully!"
                + "\nName        : {}"
                + "\nPort        : {}"
                + "\nRoutes      : farmer | crop | market"
                + "\nHealth URL  : http://localhost:{}/actuator/health"
                + "\nGateway URL : http://localhost:{}/actuator/gateway/routes"
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
//`GatewayConfig` vs `application.properties` routes — we defined routes in BOTH places. In real projects pick one. Java config gives more control, properties file is simpler. We keep both for learning.
//
//`addRequestHeader("X-Service-Name")` — gateway adds a header telling downstream service which gateway routed the request. Useful for debugging.
//
//`CorsWebFilter` — notice it's `reactive` package not regular `web` package. Gateway uses **Spring WebFlux** (reactive/non-blocking) not Spring MVC. That's why we couldn't add Spring Web dependency.
//
//`LoggingFilter implements GlobalFilter` — this runs for **every single request** through gateway. You'll see every API call logged with method, path, status, and response time in milliseconds.
//
//`getOrder() return -1` — negative number means this filter runs before all other filters. First thing that executes on every request.
//
//`Mono.fromRunnable` — this is reactive programming. Instead of blocking and waiting, we attach a callback that runs after response is sent. You'll learn more about this if you explore WebFlux.
//
//---
//
//**Your complete api-gateway:**
//```
//api-gateway/
//├── config/
//│   ├── GatewayConfig.java      ✅
//│   └── CorsConfig.java         ✅
//├── filter/
//│   └── LoggingFilter.java      ✅
//├── resources/
//│   └── application.properties  ✅
//└── ApiGatewayApplication.java  ✅
//```
//
//---
//
//**Now start all five services:**
//```
//1. docker-compose up -d
//2. FarmerServiceApplication    → port 8081
//3. AlertServiceApplication     → port 8084
//4. CropServiceApplication      → port 8082
//5. MarketServiceApplication    → port 8083
//6. ApiGatewayApplication       → port 8080
//```
//
//**Now test through gateway instead of direct ports:**
//```
//Before (direct):
//POST http://localhost:8081/api/v1/farmers/register
//
//After (through gateway):
//POST http://localhost:8080/api/v1/farmers/register
//```
//
//Both should work! Gateway routes to correct service automatically.
//
//---
//
//**Check all routes in browser:**
//```
//http://localhost:8080/actuator/gateway/routes
//```
//
//This shows all configured routes in JSON format.
//
//---
//
//**Your AgriConnect architecture is now complete:**
//```
//                    ┌─────────────┐
//     All Requests   │  API Gateway │
//    ──────────────▶ │   :8080     │
//                    └──────┬──────┘
//                           │
//            ┌──────────────┼──────────────┐
//            │              │              │
//     ┌──────▼─────┐ ┌──────▼─────┐ ┌─────▼──────┐
//     │  farmer    │ │   crop     │ │  market    │
//     │  service  │ │  service   │ │  service   │
//     │  :8081    │ │  :8082     │ │  :8083     │
//     └──────┬────┘ └──────┬─────┘ └─────┬──────┘
//            │             │             │
//            └─────────────▼─────────────┘
//                          │
//                    ┌─────▼──────┐
//                    │   Kafka    │
//                    └─────┬──────┘
//                          │
//                    ┌─────▼──────┐
//                    │   alert    │
//                    │  service   │
//                    │   :8084    │
//                    └────────────┘