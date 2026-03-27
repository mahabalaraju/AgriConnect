package com.agriconnect.marketservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "buyer_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "listing_id")
    private String listingId;

    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @Column(name = "buyer_phone", nullable = false)
    private String buyerPhone;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "crop_type")
    private String cropType;

    @Column(name = "quantity_required_kg")
    private Double quantityRequiredKg;

    @Column(name = "offered_price_per_kg")
    private Double offeredPricePerKg;

    @Column(name = "district")
    private String district;

    @Column(name = "state")
    private String state;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status")
    private ListingStatus listingStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.listingStatus = ListingStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ListingStatus {
        ACTIVE,
        FULFILLED,
        EXPIRED,
        CANCELLED
    }
}