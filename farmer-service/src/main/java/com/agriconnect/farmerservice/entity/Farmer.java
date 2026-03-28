package com.agriconnect.farmerservice.entity;

	import jakarta.persistence.*;
	import lombok.*;
	import java.time.LocalDateTime;
	import java.util.List;

	@Entity
	@Table(name = "farmers")
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public class Farmer {

	    @Id
	    @GeneratedValue(strategy = GenerationType.UUID)
	    @Column(name = "farmer_id")
	    private String farmerId;

	    @Column(name = "full_name", nullable = false)
	    private String fullName;

	    @Column(name = "phone_number", nullable = false, unique = true)
	    private String phoneNumber;

	    @Column(name = "village")
	    private String village;

	    @Column(name = "taluk")
	    private String taluk;

	    @Column(name = "district")
	    private String district;

	    @Column(name = "state")
	    private String state;

	    @Column(name = "total_land_acres")
	    private Double totalLandAcres;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "preferred_language")
	    private Language preferredLanguage;

	    @Column(name = "created_at")
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;

	    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<FarmLand> farmLands;

	    @PrePersist
	    public void prePersist() {
	        this.createdAt = LocalDateTime.now();
	        this.updatedAt = LocalDateTime.now();
	    }

	    @PreUpdate
	    public void preUpdate() {
	        this.updatedAt = LocalDateTime.now();
	    }

	    public enum Language {
	        KANNADA, ENGLISH, HINDI, TAMIL, TELUGU
	    }
	}
