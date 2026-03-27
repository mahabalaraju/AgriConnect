package com.agriconnect.cropservice.controller;

import com.agriconnect.cropservice.dto.*;
import com.agriconnect.cropservice.service.CropService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crops")
@RequiredArgsConstructor
@Slf4j
public class CropController {

    private final CropService cropService;

    // ─────────────────────────────────────────
    // CROP ENDPOINTS
    // ─────────────────────────────────────────

    // Add Crop
    @PostMapping("/add")
    public ResponseEntity<CropResponseDTO> addCrop(
            @Valid @RequestBody CropRequestDTO cropRequestDTO) {
        log.info("REST request to add crop for farmerID: {}",
                cropRequestDTO.getFarmerId());
        CropResponseDTO response = cropService.addCrop(cropRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get Crop by ID
    @GetMapping("/{cropId}")
    public ResponseEntity<CropResponseDTO> getCropById(
            @PathVariable String cropId) {
        log.info("REST request to get crop: {}", cropId);
        CropResponseDTO response = cropService.getCropById(cropId);
        return ResponseEntity.ok(response);
    }

    // Get All Crops by Farmer
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<CropResponseDTO>> getCropsByFarmer(
            @PathVariable String farmerId) {
        log.info("REST request to get crops for farmerID: {}", farmerId);
        List<CropResponseDTO> response = cropService.getCropsByFarmer(farmerId);
        return ResponseEntity.ok(response);
    }

    // Update Crop Status
    @PatchMapping("/{cropId}/status")
    public ResponseEntity<CropResponseDTO> updateCropStatus(
            @PathVariable String cropId,
            @Valid @RequestBody CropStatusUpdateDTO statusUpdateDTO) {
        log.info("REST request to update crop status: {} to {}",
                cropId, statusUpdateDTO.getCropStatus());
        CropResponseDTO response = cropService
                .updateCropStatus(cropId, statusUpdateDTO);
        return ResponseEntity.ok(response);
    }

    // Delete Crop
    @DeleteMapping("/{cropId}")
    public ResponseEntity<String> deleteCrop(
            @PathVariable String cropId) {
        log.info("REST request to delete crop: {}", cropId);
        cropService.deleteCrop(cropId);
        return ResponseEntity.ok("Crop deleted successfully");
    }

    // ─────────────────────────────────────────
    // EXPENSE ENDPOINTS
    // ─────────────────────────────────────────

    // Add Expense
    @PostMapping("/{cropId}/expenses")
    public ResponseEntity<CropExpenseResponseDTO> addExpense(
            @PathVariable String cropId,
            @Valid @RequestBody CropExpenseRequestDTO expenseRequestDTO) {
        log.info("REST request to add expense for cropID: {}", cropId);
        CropExpenseResponseDTO response = cropService
                .addExpense(cropId, expenseRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get All Expenses by Crop
    @GetMapping("/{cropId}/expenses")
    public ResponseEntity<List<CropExpenseResponseDTO>> getExpensesByCrop(
            @PathVariable String cropId) {
        log.info("REST request to get expenses for cropID: {}", cropId);
        List<CropExpenseResponseDTO> response = cropService
                .getExpensesByCrop(cropId);
        return ResponseEntity.ok(response);
    }

    // Get Total Expenses
    @GetMapping("/{cropId}/expenses/total")
    public ResponseEntity<Double> getTotalExpenses(
            @PathVariable String cropId) {
        log.info("REST request to get total expenses for cropID: {}", cropId);
        Double total = cropService.getTotalExpenses(cropId);
        return ResponseEntity.ok(total);
    }

    // ─────────────────────────────────────────
    // ANALYTICS ENDPOINTS
    // ─────────────────────────────────────────

    // Calculate Profit Loss
    @GetMapping("/{cropId}/profit-loss")
    public ResponseEntity<Double> calculateProfitLoss(
            @PathVariable String cropId) {
        log.info("REST request to calculate P&L for cropID: {}", cropId);
        Double profitLoss = cropService.calculateProfitLoss(cropId);
        return ResponseEntity.ok(profitLoss);
    }
}