package com.agriconnect.farmerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "farm_lands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmLand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "land_id")
    private String landId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(name = "survey_number")
    private String surveyNumber;

    @Column(name = "acres", nullable = false)
    private Double acres;

    @Enumerated(EnumType.STRING)
    @Column(name = "soil_type")
    private SoilType soilType;

    @Enumerated(EnumType.STRING)
    @Column(name = "irrigation_type")
    private IrrigationType irrigationType;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

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

    public enum SoilType {
        RED, BLACK, ALLUVIAL, LATERITE, SANDY
    }

    public enum IrrigationType {
        RAIN_FED, DRIP, CANAL, BOREWELL, TANK
    }
}
