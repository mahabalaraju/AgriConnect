package com.agriconnect.farmerservice.repo;

import com.agriconnect.farmerservice.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, String> {

    // Find farmer by phone number
    Optional<Farmer> findByPhoneNumber(String phoneNumber);

    // Check if phone number already exists
    boolean existsByPhoneNumber(String phoneNumber);

    // Find all farmers in a district
    List<Farmer> findByDistrict(String district);

    // Find all farmers in a village
    List<Farmer> findByVillage(String village);

    // Find farmers by district and village
    List<Farmer> findByDistrictAndVillage(String district, String village);

    // Custom query - find farmers with land more than given acres
    @Query("SELECT f FROM Farmer f WHERE f.totalLandAcres >= :acres")
    List<Farmer> findFarmersWithMinLand(Double acres);
}