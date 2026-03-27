package com.agriconnect.cropservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "crop_id")
    private String cropId;

    @Column(name = "farmer_id", nullable = false)
    private String farmerId;

    @Column(name = "land_id")
    private String landId;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_type")
    private CropType cropType;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_status")
    private CropStatus cropStatus;

    @Column(name = "sowing_date")
    private LocalDate sowingDate;

    @Column(name = "expected_harvest_date")
    private LocalDate expectedHarvestDate;

    @Column(name = "actual_harvest_date")
    private LocalDate actualHarvestDate;

    @Column(name = "area_in_acres")
    private Double areaInAcres;

    @Column(name = "expected_yield_kg")
    private Double expectedYieldKg;

    @Column(name = "actual_yield_kg")
    private Double actualYieldKg;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.cropStatus = CropStatus.PLANNED;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum CropType {
        CEREAL,         // Rice, Wheat, Maize
        PULSE,          // Dal, Lentils
        OILSEED,        // Sunflower, Groundnut
        VEGETABLE,
        FRUIT,
        SPICE,
        CASH_CROP       // Sugarcane, Cotton, Tobacco
    }

    public enum CropStatus {
        PLANNED,        // Farmer plans to sow
        SOWED,          // Seed sown in field
        GROWING,        // Crop is growing
        FLOWERING,      // Flowering stage
        HARVESTING,     // Ready to harvest
        HARVESTED,      // Harvest complete
        DISTRESS        // Crop damage / disease
    }
}