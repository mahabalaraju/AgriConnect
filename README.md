# AgriConnect 🌾

Agri-Tech Microservices Platform connecting farmers
to buyers directly — cutting middlemen and empowering
Karnataka farmers with real-time market intelligence.

## Services

| Service | Port | Status | Description |
|---------|------|--------|-------------|
| api-gateway | 8080 | ✅ Live | Single entry point for all services |
| farmer-service | 8081 | ✅ Live | Farmer registration and land management |
| crop-service | 8082 | ✅ Live | Crop lifecycle tracking and expense management |
| market-service | 8083 | ✅ Live | Mandi prices, buyer listings and harvest records |
| alert-service | 8084 | ✅ Live | Kafka-driven real-time notifications |

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Messaging | Apache Kafka |
| Database | MySQL 8.0 |
| Gateway | Spring Cloud Gateway |
| Containerization | Docker |
| Build Tool | Maven |

## Architecture
```
                    ┌─────────────────┐
     All Requests   │   API Gateway   │
    ──────────────▶ │     :8080       │
                    └────────┬────────┘
                             │
             ┌───────────────┼───────────────┐
             │               │               │
     ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼───────┐
     │   farmer     │ │    crop     │ │   market    │
     │   service    │ │   service   │ │   service   │
     │   :8081      │ │   :8082     │ │   :8083     │
     └───────┬──────┘ └──────┬──────┘ └─────┬───────┘
             │               │               │
             └───────────────▼───────────────┘
                             │
                       ┌─────▼──────┐
                       │   Kafka    │
                       └─────┬──────┘
                             │
                       ┌─────▼──────┐
                       │   alert    │
                       │  service   │
                       │   :8084    │
                       └────────────┘
```

## Kafka Event Flow

| Event | Producer | Consumers |
|-------|----------|-----------|
| farmer.registered | farmer-service | alert-service, crop-service |
| crop.sowed | crop-service | alert-service |
| crop.harvested | crop-service | market-service |
| crop.distress | crop-service | alert-service |
| price.updated | market-service | alert-service |

## Key Features

- Farmer registration with land management
- Full crop lifecycle tracking — sowing to harvest
- Expense tracking and profit/loss analytics
- Real-time mandi price updates
- Buyer listings — farmers connect directly with buyers
- Suggested selling price based on 7-day market average
- Kafka-driven instant notifications for price changes
- District-wise hyperlocal intelligence for Karnataka

## How to Run
```bash
# Start infrastructure
docker-compose up -d

# Services start order
# 1. farmer-service  :8081
# 2. alert-service   :8084
# 3. crop-service    :8082
# 4. market-service  :8083
# 5. api-gateway     :8080
```

## API Endpoints

### Farmer Service
```
POST   /api/v1/farmers/register
GET    /api/v1/farmers/{farmerId}
PUT    /api/v1/farmers/{farmerId}
DELETE /api/v1/farmers/{farmerId}
GET    /api/v1/farmers/district/{district}
POST   /api/v1/farmers/{farmerId}/lands
```

### Crop Service
```
POST   /api/v1/crops/add
GET    /api/v1/crops/{cropId}
GET    /api/v1/crops/farmer/{farmerId}
PATCH  /api/v1/crops/{cropId}/status
POST   /api/v1/crops/{cropId}/expenses
GET    /api/v1/crops/{cropId}/profit-loss
```

### Market Service
```
POST   /api/v1/market/prices
GET    /api/v1/market/prices/latest?cropName=Rice&district=Mandya
GET    /api/v1/market/listings/best-price/{cropName}
GET    /api/v1/market/analytics/suggested-price
GET    /api/v1/market/harvests/farmer/{farmerId}
```

## Roadmap

- [x] farmer-service
- [x] alert-service
- [x] crop-service
- [x] market-service
- [x] api-gateway
- [ ] Swagger documentation
- [ ] Docker — containerize all services
- [ ] Eureka service discovery
- [ ] Zipkin distributed tracing
- [ ] Scheduled jobs — auto harvest alerts
- [ ] Config server

## Author

**H Mahabalaraju**
Java Backend Developer | Bengaluru
LinkedIn : https://www.linkedin.com/in/mahabalaraju-h-043298191
GitHub : https://github.com/mahabalaraju
