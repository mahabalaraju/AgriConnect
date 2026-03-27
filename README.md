# AgriConnect 🌾

Agri-Tech Microservices Platform connecting farmers 
to buyers directly.

## Services
| Service | Port | Description |
|---------|------|-------------|
| farmer-service | 8081 | Farmer registration and land management |
| alert-service | 8084 | Kafka-driven notifications (coming soon) |
| crop-service | 8082 | Crop lifecycle tracking (coming soon) |
| market-service | 8083 | Mandi prices and buyer connect (coming soon) |

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Apache Kafka
- MySQL 8.0
- Docker

## How to Run
```bash
docker-compose up -d
```

## Architecture

farmer-service → Kafka → alert-service
                       → crop-service
                       → market-service
