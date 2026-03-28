package com.agriconnect.farmerservice.service;

import com.agriconnect.farmerservice.dto.FarmerRequestDTO;
import com.agriconnect.farmerservice.dto.FarmerResponseDTO;
import com.agriconnect.farmerservice.dto.LandRequestDTO;
import com.agriconnect.farmerservice.dto.LandResponseDTO;
import java.util.List;

public interface FarmerService {

    // Farmer operations
    FarmerResponseDTO registerFarmer(FarmerRequestDTO farmerRequestDTO);
    FarmerResponseDTO getFarmerById(String farmerId);
    FarmerResponseDTO getFarmerByPhone(String phoneNumber);
    FarmerResponseDTO updateFarmer(String farmerId, FarmerRequestDTO farmerRequestDTO);
    void deleteFarmer(String farmerId);
    List<FarmerResponseDTO> getFarmersByDistrict(String district);
    List<FarmerResponseDTO> getFarmersByVillage(String village);

    // Land operations
    LandResponseDTO addLand(String farmerId, LandRequestDTO landRequestDTO);
    List<LandResponseDTO> getLandsByFarmer(String farmerId);
    LandResponseDTO updateLand(String landId, LandRequestDTO landRequestDTO);
    void deleteLand(String landId);
}