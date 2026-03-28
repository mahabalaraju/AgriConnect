package com.agriconnect.farmerservice.controller;

import com.agriconnect.farmerservice.dto.LandRequestDTO;
import com.agriconnect.farmerservice.dto.LandResponseDTO;
import com.agriconnect.farmerservice.service.FarmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/farmers")
@RequiredArgsConstructor
@Slf4j
public class LandController {

	private static final Logger log = 
		    LoggerFactory.getLogger(LandController.class);
	
	@Autowired
    private FarmerService farmerService;

    // Add Land to Farmer
    @PostMapping("/{farmerId}/lands")
    public ResponseEntity<LandResponseDTO> addLand(
            @PathVariable String farmerId,
            @Valid @RequestBody LandRequestDTO landRequestDTO) {
        log.info("REST request to add land for farmer: {}", farmerId);
        LandResponseDTO response = farmerService.addLand(farmerId, landRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get All Lands of Farmer
    @GetMapping("/{farmerId}/lands")
    public ResponseEntity<List<LandResponseDTO>> getLandsByFarmer(
            @PathVariable String farmerId) {
        log.info("REST request to get lands for farmer: {}", farmerId);
        List<LandResponseDTO> response = farmerService.getLandsByFarmer(farmerId);
        return ResponseEntity.ok(response);
    }

    // Update Land
    @PutMapping("/{farmerId}/lands/{landId}")
    public ResponseEntity<LandResponseDTO> updateLand(
            @PathVariable String farmerId,
            @PathVariable String landId,
            @Valid @RequestBody LandRequestDTO landRequestDTO) {
        log.info("REST request to update land: {} for farmer: {}", landId, farmerId);
        LandResponseDTO response = farmerService.updateLand(landId, landRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Delete Land
    @DeleteMapping("/{farmerId}/lands/{landId}")
    public ResponseEntity<String> deleteLand(
            @PathVariable String farmerId,
            @PathVariable String landId) {
        log.info("REST request to delete land: {} for farmer: {}", landId, farmerId);
        farmerService.deleteLand(landId);
        return ResponseEntity.ok("Land deleted successfully");
    }
}
//
//
//**Now also remove land endpoints from `FarmerController.java`** — since we moved them here. Your `FarmerController` should only have farmer related endpoints:
//```
//FarmerController  →  register, getById, getByPhone, 
//                     update, delete, byDistrict, byVillage
//
//LandController    →  addLand, getLands, 
//                     updateLand, deleteLand
//```
