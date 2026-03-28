package com.agriconnect.farmerservice.repo;

import com.agriconnect.farmerservice.entity.FarmLand;
import com.agriconnect.farmerservice.entity.FarmLand.IrrigationType;
import com.agriconnect.farmerservice.entity.FarmLand.SoilType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LandRepository extends JpaRepository<FarmLand, String> {

    // Find all lands belonging to a farmer
    List<FarmLand> findByFarmer_FarmerId(String farmerId);

    // Find lands by soil type
    List<FarmLand> findBySoilType(SoilType soilType);

    // Find lands by irrigation type
    List<FarmLand> findByIrrigationType(IrrigationType irrigationType);

    // Check if survey number already exists
    boolean existsBySurveyNumber(String surveyNumber);
}