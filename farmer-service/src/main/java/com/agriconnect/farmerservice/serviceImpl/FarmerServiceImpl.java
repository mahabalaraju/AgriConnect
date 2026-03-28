package com.agriconnect.farmerservice.serviceImpl;

import com.agriconnect.farmerservice.dto.*;
import com.agriconnect.farmerservice.entity.Farmer;
import com.agriconnect.farmerservice.entity.FarmLand;
import com.agriconnect.farmerservice.exception.FarmerAlreadyExistsException;
import com.agriconnect.farmerservice.exception.FarmerNotFoundException;
import com.agriconnect.farmerservice.exception.LandNotFoundException;
import com.agriconnect.farmerservice.kafka.FarmerEventProducer;
import com.agriconnect.farmerservice.repo.FarmerRepository;
import com.agriconnect.farmerservice.repo.LandRepository;
import com.agriconnect.farmerservice.service.FarmerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository farmerRepository;
    private final LandRepository landRepository;
    private final FarmerEventProducer farmerEventProducer;

    // ─────────────────────────────────────────
    // FARMER OPERATIONS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public FarmerResponseDTO registerFarmer(FarmerRequestDTO dto) {
        log.info("Registering farmer with phone: {}", dto.getPhoneNumber());

        // Check duplicate phone number
        if (farmerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new FarmerAlreadyExistsException(
                "Farmer already exists with phone: " + dto.getPhoneNumber()
            );
        }

        // Map DTO to Entity
        Farmer farmer = Farmer.builder()
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .village(dto.getVillage())
                .taluk(dto.getTaluk())
                .district(dto.getDistrict())
                .state(dto.getState())
                .totalLandAcres(dto.getTotalLandAcres())
                .preferredLanguage(dto.getPreferredLanguage())
                .build();

        Farmer savedFarmer = farmerRepository.save(farmer);

        // Save farm lands if provided
        if (dto.getFarmLands() != null && !dto.getFarmLands().isEmpty()) {
            List<FarmLand> lands = dto.getFarmLands().stream()
                    .map(landDTO -> mapToLandEntity(landDTO, savedFarmer))
                    .collect(Collectors.toList());
            landRepository.saveAll(lands);
            savedFarmer.setFarmLands(lands);
        }

        // Publish Kafka event
        farmerEventProducer.publishFarmerRegistered(savedFarmer);

        log.info("Farmer registered successfully with ID: {}", savedFarmer.getFarmerId());
        return mapToFarmerResponse(savedFarmer);
    }

    @Override
    public FarmerResponseDTO getFarmerById(String farmerId) {
        log.info("Fetching farmer with ID: {}", farmerId);
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException(
                    "Farmer not found with ID: " + farmerId
                ));
        return mapToFarmerResponse(farmer);
    }

    @Override
    public FarmerResponseDTO getFarmerByPhone(String phoneNumber) {
        Farmer farmer = farmerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new FarmerNotFoundException(
                    "Farmer not found with phone: " + phoneNumber
                ));
        return mapToFarmerResponse(farmer);
    }

    @Override
    @Transactional
    public FarmerResponseDTO updateFarmer(String farmerId, FarmerRequestDTO dto) {
        log.info("Updating farmer with ID: {}", farmerId);
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException(
                    "Farmer not found with ID: " + farmerId
                ));

        farmer.setFullName(dto.getFullName());
        farmer.setVillage(dto.getVillage());
        farmer.setTaluk(dto.getTaluk());
        farmer.setDistrict(dto.getDistrict());
        farmer.setState(dto.getState());
        farmer.setTotalLandAcres(dto.getTotalLandAcres());
        farmer.setPreferredLanguage(dto.getPreferredLanguage());

        Farmer updatedFarmer = farmerRepository.save(farmer);
        log.info("Farmer updated successfully: {}", farmerId);
        return mapToFarmerResponse(updatedFarmer);
    }

    @Override
    @Transactional
    public void deleteFarmer(String farmerId) {
        log.info("Deleting farmer with ID: {}", farmerId);
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException(
                    "Farmer not found with ID: " + farmerId
                ));
        farmerRepository.delete(farmer);
        log.info("Farmer deleted successfully: {}", farmerId);
    }

    @Override
    public List<FarmerResponseDTO> getFarmersByDistrict(String district) {
        return farmerRepository.findByDistrict(district)
                .stream()
                .map(this::mapToFarmerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FarmerResponseDTO> getFarmersByVillage(String village) {
        return farmerRepository.findByVillage(village)
                .stream()
                .map(this::mapToFarmerResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // LAND OPERATIONS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public LandResponseDTO addLand(String farmerId, LandRequestDTO dto) {
        log.info("Adding land for farmer ID: {}", farmerId);
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException(
                    "Farmer not found with ID: " + farmerId
                ));

        FarmLand land = mapToLandEntity(dto, farmer);
        FarmLand savedLand = landRepository.save(land);
        log.info("Land added successfully with ID: {}", savedLand.getLandId());
        return mapToLandResponse(savedLand);
    }

    @Override
    public List<LandResponseDTO> getLandsByFarmer(String farmerId) {
        return landRepository.findByFarmer_FarmerId(farmerId)
                .stream()
                .map(this::mapToLandResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LandResponseDTO updateLand(String landId, LandRequestDTO dto) {
        FarmLand land = landRepository.findById(landId)
                .orElseThrow(() -> new LandNotFoundException(
                    "Land not found with ID: " + landId
                ));

        land.setSurveyNumber(dto.getSurveyNumber());
        land.setAcres(dto.getAcres());
        land.setSoilType(dto.getSoilType());
        land.setIrrigationType(dto.getIrrigationType());
        land.setLatitude(dto.getLatitude());
        land.setLongitude(dto.getLongitude());

        return mapToLandResponse(landRepository.save(land));
    }

    @Override
    @Transactional
    public void deleteLand(String landId) {
        FarmLand land = landRepository.findById(landId)
                .orElseThrow(() -> new LandNotFoundException(
                    "Land not found with ID: " + landId
                ));
        landRepository.delete(land);
    }

    // ─────────────────────────────────────────
    // MAPPING METHODS
    // ─────────────────────────────────────────

    private FarmerResponseDTO mapToFarmerResponse(Farmer farmer) {
        List<LandResponseDTO> landDTOs = null;
        if (farmer.getFarmLands() != null) {
            landDTOs = farmer.getFarmLands()
                    .stream()
                    .map(this::mapToLandResponse)
                    .collect(Collectors.toList());
        }

        return FarmerResponseDTO.builder()
                .farmerId(farmer.getFarmerId())
                .fullName(farmer.getFullName())
                .phoneNumber(farmer.getPhoneNumber())
                .village(farmer.getVillage())
                .taluk(farmer.getTaluk())
                .district(farmer.getDistrict())
                .state(farmer.getState())
                .totalLandAcres(farmer.getTotalLandAcres())
                .preferredLanguage(farmer.getPreferredLanguage())
                .createdAt(farmer.getCreatedAt())
                .farmLands(landDTOs)
                .build();
    }

    private FarmLand mapToLandEntity(LandRequestDTO dto, Farmer farmer) {
        return FarmLand.builder()
                .farmer(farmer)
                .surveyNumber(dto.getSurveyNumber())
                .acres(dto.getAcres())
                .soilType(dto.getSoilType())
                .irrigationType(dto.getIrrigationType())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    private LandResponseDTO mapToLandResponse(FarmLand land) {
        return LandResponseDTO.builder()
                .landId(land.getLandId())
                .surveyNumber(land.getSurveyNumber())
                .acres(land.getAcres())
                .soilType(land.getSoilType())
                .irrigationType(land.getIrrigationType())
                .latitude(land.getLatitude())
                .longitude(land.getLongitude())
                .createdAt(land.getCreatedAt())
                .build();
    }
}