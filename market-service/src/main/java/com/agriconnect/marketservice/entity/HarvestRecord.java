package com.agriconnect.marketservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "harvest_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "record_id")
    private String recordId;

    @Column(name = "crop_id", nullable = false)
    private String cropId;

    @Column(name = "farmer_id", nullable = false)
    private String farmerId;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "crop_type")
    private String cropType;

    @Column(name = "actual_yield_kg")
    private Double actualYieldKg;

    @Column(name = "expected_yield_kg")
    private Double expectedYieldKg;

    @Column(name = "total_expenses")
    private Double totalExpenses;

    @Column(name = "suggested_price_per_kg")
    private Double suggestedPricePerKg;

    @Column(name = "harvest_date")
    private LocalDate harvestDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sell_status")
    private SellStatus sellStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.sellStatus = SellStatus.AVAILABLE;
    }

    public enum SellStatus {
        AVAILABLE,
        PARTIALLY_SOLD,
        SOLD
    }
}
//```
//
//---
//
//**Key things to notice:**
//
//`MarketPrice` has `minPricePerKg`, `maxPricePerKg`, `modalPricePerKg` — this is exactly how government mandi price data is structured. Modal price is the most common trading price — most useful for farmers.
//
//`PriceTrend` — RISING, FALLING, STABLE. When alert-service gets `price.updated` event with RISING trend for Rice in Mandya, it notifies all Rice farmers in Mandya district. Real value!
//
//`BuyerListing` — buyers post what crop they want, quantity, and price they'll pay. Farmers browse and connect directly. This is the marketplace core — cutting middlemen.
//
//`HarvestRecord` — created automatically when crop-service publishes `crop.harvested` event. market-service receives it and suggests a selling price based on current mandi rates. Farmer gets instant price recommendation!
//
//`suggestedPricePerKg` — calculated by market-service based on current `MarketPrice` data for that crop in that district. This is your AI-powered pricing feature.
//
//---
//
//**Your entity layer:**
//```
//entity/
//├── MarketPrice.java      ✅
//├── BuyerListing.java     ✅
//└── HarvestRecord.java    ✅