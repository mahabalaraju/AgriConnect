package com.agriconnect.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Farmer Service Routes
                .route("farmer-service", r -> r
                        .path("/api/v1/farmers/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service-Name",
                                        "farmer-service")
                                .addResponseHeader("X-Gateway",
                                        "AgriConnect-Gateway"))
                        .uri("http://localhost:8081"))

                // Crop Service Routes
                .route("crop-service", r -> r
                        .path("/api/v1/crops/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service-Name",
                                        "crop-service")
                                .addResponseHeader("X-Gateway",
                                        "AgriConnect-Gateway"))
                        .uri("http://localhost:8082"))

                // Market Service Routes
                .route("market-service", r -> r
                        .path("/api/v1/market/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service-Name",
                                        "market-service")
                                .addResponseHeader("X-Gateway",
                                        "AgriConnect-Gateway"))
                        .uri("http://localhost:8083"))

                .build();
    }
}