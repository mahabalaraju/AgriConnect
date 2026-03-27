package com.agriconnect.cropservice.serviceimpl;

import com.agriconnect.cropservice.dto.*;
import com.agriconnect.cropservice.entity.Crop;
import com.agriconnect.cropservice.entity.Crop.CropStatus;
import com.agriconnect.cropservice.entity.CropExpense;
import com.agriconnect.cropservice.exception.CropNotFoundException;
import com.agriconnect.cropservice.exception.InvalidStatusTransitionException;
import com.agriconnect.cropservice.producer.CropEventProducer;
import com.agriconnect.cropservice.repository.CropExpenseRepository;
import com.agriconnect.cropservice.repository.CropRepository;
import com.agriconnect.cropservice.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final CropExpenseRepository cropExpenseRepository;
    private final CropEventProducer cropEventProducer;

    // ─────────────────────────────────────────
    // CROP OPERATIONS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public CropResponseDTO addCrop(CropRequestDTO dto) {
        log.info("Adding crop: {} for farmerID: {}", 
                dto.getCropName(), dto.getFarmerId());

        Crop crop = Crop.builder()
                .farmerId(dto.getFarmerId())
                .landId(dto.getLandId())
                .cropName(dto.getCropName())
                .cropType(dto.getCropType())
                .sowingDate(dto.getSowingDate())
                .expectedHarvestDate(dto.getExpectedHarvestDate())
                .areaInAcres(dto.getAreaInAcres())
                .expectedYieldKg(dto.getExpectedYieldKg())
                .notes(dto.getNotes())
                .build();

        Crop savedCrop = cropRepository.save(crop);

        // Publish crop.sowed event
        cropEventProducer.publishCropSowed(savedCrop);

        log.info("Crop added successfully: {}", savedCrop.getCropId());
        return mapToCropResponse(savedCrop);
    }

    @Override
    public CropResponseDTO getCropById(String cropId) {
        log.info("Fetching crop: {}", cropId);
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException(
                    "Crop not found with ID: " + cropId));
        return mapToCropResponse(crop);
    }

    @Override
    public List<CropResponseDTO> getCropsByFarmer(String farmerId) {
        log.info("Fetching crops for farmerID: {}", farmerId);
        return cropRepository.findByFarmerId(farmerId)
                .stream()
                .map(this::mapToCropResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropResponseDTO updateCropStatus(String cropId,
            CropStatusUpdateDTO dto) {
        log.info("Updating crop status: {} to {}", cropId, dto.getCropStatus());

        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException(
                    "Crop not found with ID: " + cropId));

        // Validate status transition
        validateStatusTransition(crop.getCropStatus(), dto.getCropStatus());

        crop.setCropStatus(dto.getCropStatus());

        // Set harvest details if harvested
        if (dto.getCropStatus() == CropStatus.HARVESTED) {
            crop.setActualHarvestDate(dto.getActualHarvestDate());
            crop.setActualYieldKg(dto.getActualYieldKg());

            // Publish crop.harvested event
            Double totalExpenses = cropExpenseRepository
                    .calculateTotalExpenses(cropId);
            cropEventProducer.publishCropHarvested(crop, 
                    totalExpenses != null ? totalExpenses : 0.0);
        }

        // Publish crop.distress event if distress
        if (dto.getCropStatus() == CropStatus.DISTRESS) {
            crop.setNotes(dto.getNotes());
            cropEventProducer.publishCropDistress(crop, dto.getNotes());
        }

        Crop updatedCrop = cropRepository.save(crop);
        log.info("Crop status updated successfully: {}", cropId);
        return mapToCropResponse(updatedCrop);
    }

    @Override
    @Transactional
    public void deleteCrop(String cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException(
                    "Crop not found with ID: " + cropId));
        cropRepository.delete(crop);
        log.info("Crop deleted: {}", cropId);
    }

    // ─────────────────────────────────────────
    // EXPENSE OPERATIONS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public CropExpenseResponseDTO addExpense(String cropId,
            CropExpenseRequestDTO dto) {
        log.info("Adding expense for cropID: {}", cropId);

        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException(
                    "Crop not found with ID: " + cropId));

        CropExpense expense = CropExpense.builder()
                .crop(crop)
                .expenseType(dto.getExpenseType())
                .amount(dto.getAmount())
                .expenseDate(dto.getExpenseDate())
                .description(dto.getDescription())
                .build();

        CropExpense savedExpense = cropExpenseRepository.save(expense);
        log.info("Expense added: {} for cropID: {}", 
                savedExpense.getExpenseId(), cropId);
        return mapToExpenseResponse(savedExpense);
    }

    @Override
    public List<CropExpenseResponseDTO> getExpensesByCrop(String cropId) {
        return cropExpenseRepository.findByCrop_CropId(cropId)
                .stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getTotalExpenses(String cropId) {
        Double total = cropExpenseRepository.calculateTotalExpenses(cropId);
        return total != null ? total : 0.0;
    }

    // ─────────────────────────────────────────
    // ANALYTICS
    // ─────────────────────────────────────────

    @Override
    public Double calculateProfitLoss(String cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new CropNotFoundException(
                    "Crop not found with ID: " + cropId));

        Double totalExpenses = getTotalExpenses(cropId);

        // Basic profit calculation
        // In real world: integrate with market-service for current price
        if (crop.getActualYieldKg() == null) {
            log.warn("Crop not harvested yet, cannot calculate profit");
            return null;
        }

        // Assuming average price of Rs.20 per kg for now
        // Later market-service will provide real price
        double revenue = crop.getActualYieldKg() * 20.0;
        double profitLoss = revenue - totalExpenses;

        log.info("CropID: {} | Revenue: {} | Expenses: {} | P&L: {}",
                cropId, revenue, totalExpenses, profitLoss);

        return profitLoss;
    }

    // ─────────────────────────────────────────
    // STATUS TRANSITION VALIDATION
    // ─────────────────────────────────────────

    private void validateStatusTransition(CropStatus current,
            CropStatus next) {
        // Define valid transitions
        boolean valid = switch (current) {
            case PLANNED -> next == CropStatus.SOWED
                         || next == CropStatus.DISTRESS;
            case SOWED -> next == CropStatus.GROWING
                       || next == CropStatus.DISTRESS;
            case GROWING -> next == CropStatus.FLOWERING
                         || next == CropStatus.HARVESTING
                         || next == CropStatus.DISTRESS;
            case FLOWERING -> next == CropStatus.HARVESTING
                           || next == CropStatus.DISTRESS;
            case HARVESTING -> next == CropStatus.HARVESTED
                            || next == CropStatus.DISTRESS;
            case HARVESTED -> false;
            case DISTRESS -> next == CropStatus.GROWING
                          || next == CropStatus.HARVESTED;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(
                "Invalid status transition from " + current + " to " + next
            );
        }
    }

    // ─────────────────────────────────────────
    // MAPPING METHODS
    // ─────────────────────────────────────────

    private CropResponseDTO mapToCropResponse(Crop crop) {
        List<CropExpenseResponseDTO> expenses = 
                cropExpenseRepository.findByCrop_CropId(crop.getCropId())
                .stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());

        return CropResponseDTO.builder()
                .cropId(crop.getCropId())
                .farmerId(crop.getFarmerId())
                .landId(crop.getLandId())
                .cropName(crop.getCropName())
                .cropType(crop.getCropType())
                .cropStatus(crop.getCropStatus())
                .sowingDate(crop.getSowingDate())
                .expectedHarvestDate(crop.getExpectedHarvestDate())
                .actualHarvestDate(crop.getActualHarvestDate())
                .areaInAcres(crop.getAreaInAcres())
                .expectedYieldKg(crop.getExpectedYieldKg())
                .actualYieldKg(crop.getActualYieldKg())
                .notes(crop.getNotes())
                .createdAt(crop.getCreatedAt())
                .expenses(expenses)
                .build();
    }

    private CropExpenseResponseDTO mapToExpenseResponse(CropExpense expense) {
        return CropExpenseResponseDTO.builder()
                .expenseId(expense.getExpenseId())
                .expenseType(expense.getExpenseType())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
//```
//
//---
//
//**Key things to notice:**
//
//`validateStatusTransition` — this is very important. A crop cannot go from `HARVESTED` back to `GROWING`. Java 17 `switch` expression makes this clean and readable. This is **business rule enforcement** at service layer.
//
//`calculateProfitLoss` — right now uses hardcoded Rs.20/kg. Later when market-service is ready, you'll call its API to get real mandi price. This is how microservices evolve incrementally.
//
//`publishCropHarvested` passes `totalExpenses` — when crop is harvested we calculate total money spent and send it with the event. market-service will use this to show net profit to farmer.
//
//`publishCropDistress` — immediately triggers urgent notification to farmer through alert-service. Real AgriTech value!
//
//---
//
//**Your service layer:**
//```
//service/
//└── CropService.java              ✅
//
//serviceimpl/
//└── CropServiceImpl.java          ✅