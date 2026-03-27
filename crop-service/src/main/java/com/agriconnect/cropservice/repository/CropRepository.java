package com.agriconnect.cropservice.repository;

import com.agriconnect.cropservice.entity.Crop;
import com.agriconnect.cropservice.entity.Crop.CropStatus;
import com.agriconnect.cropservice.entity.Crop.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, String> {

    // Find all crops of a farmer
    List<Crop> findByFarmerId(String farmerId);

    // Find crops by farmer and status
    List<Crop> findByFarmerIdAndCropStatus(String farmerId, CropStatus cropStatus);

    // Find crops by farmer and crop type
    List<Crop> findByFarmerIdAndCropType(String farmerId, CropType cropType);

    // Find all crops with a specific status
    List<Crop> findByCropStatus(CropStatus cropStatus);

    // Find crops ready to harvest
    // (expected harvest date is today or before today and status is GROWING)
    @Query("SELECT c FROM Crop c WHERE c.expectedHarvestDate <= :today " +
           "AND c.cropStatus = 'GROWING'")
    List<Crop> findCropsReadyToHarvest(LocalDate today);

    // Find distressed crops
    List<Crop> findByCropStatusAndFarmerId(CropStatus cropStatus, String farmerId);

    // Count crops by farmer
    long countByFarmerId(String farmerId);

    // Find crops by land
    List<Crop> findByLandId(String landId);
}