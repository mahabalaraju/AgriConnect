package com.agriconnect.marketservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "price_id")
    private String priceId;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "crop_type")
    private String cropType;

    @Column(name = "mandi_name", nullable = false)
    private String mandiName;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "state")
    private String state;

    @Column(name = "min_price_per_kg")
    private Double minPricePerKg;

    @Column(name = "max_price_per_kg")
    private Double maxPricePerKg;

    @Column(name = "modal_price_per_kg")
    private Double modalPricePerKg;

    @Column(name = "price_date", nullable = false)
    private LocalDate priceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_trend")
    private PriceTrend priceTrend;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum PriceTrend {
        RISING,
        FALLING,
        STABLE
    }
}