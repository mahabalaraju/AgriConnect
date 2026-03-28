package com.agriconnect.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        log.info("Incoming Request | " +
                 "Method: {} | " +
                 "Path: {} | " +
                 "Time: {}",
                request.getMethod(),
                request.getPath(),
                LocalDateTime.now());

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long duration = System.currentTimeMillis() - startTime;

                    log.info("Outgoing Response | " +
                             "Path: {} | " +
                             "Status: {} | " +
                             "Duration: {}ms",
                            request.getPath(),
                            response.getStatusCode(),
                            duration);
                })
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}