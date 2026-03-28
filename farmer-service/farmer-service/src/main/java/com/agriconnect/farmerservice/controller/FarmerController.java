package com.agriconnect.farmerservice.controller;

import com.agriconnect.farmerservice.dto.FarmerRequestDTO;
import com.agriconnect.farmerservice.dto.FarmerResponseDTO;
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
public class FarmerController {

	private static final Logger log = 
		    LoggerFactory.getLogger(FarmerController.class);
	@Autowired
    private  FarmerService farmerService;

    // ─────────────────────────────────────────
    // FARMER ENDPOINTS
    // ─────────────────────────────────────────

    // Register Farmer
    @PostMapping("/register")
    public ResponseEntity<FarmerResponseDTO> registerFarmer(
            @Valid @RequestBody FarmerRequestDTO farmerRequestDTO) {
        log.info("REST request to register farmer: {}", farmerRequestDTO.getPhoneNumber());
        FarmerResponseDTO response = farmerService.registerFarmer(farmerRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get Farmer by ID
    @GetMapping("/{farmerId}")
    public ResponseEntity<FarmerResponseDTO> getFarmerById(
            @PathVariable String farmerId) {
        log.info("REST request to get farmer by ID: {}", farmerId);
        FarmerResponseDTO response = farmerService.getFarmerById(farmerId);
        return ResponseEntity.ok(response);
    }

    // Get Farmer by Phone
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<FarmerResponseDTO> getFarmerByPhone(
            @PathVariable String phoneNumber) {
        log.info("REST request to get farmer by phone: {}", phoneNumber);
        FarmerResponseDTO response = farmerService.getFarmerByPhone(phoneNumber);
        return ResponseEntity.ok(response);
    }

    // Update Farmer
    @PutMapping("/{farmerId}")
    public ResponseEntity<FarmerResponseDTO> updateFarmer(
            @PathVariable String farmerId,
            @Valid @RequestBody FarmerRequestDTO farmerRequestDTO) {
        log.info("REST request to update farmer: {}", farmerId);
        FarmerResponseDTO response = farmerService.updateFarmer(farmerId, farmerRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Delete Farmer
    @DeleteMapping("/{farmerId}")
    public ResponseEntity<String> deleteFarmer(
            @PathVariable String farmerId) {
        log.info("REST request to delete farmer: {}", farmerId);
        farmerService.deleteFarmer(farmerId);
        return ResponseEntity.ok("Farmer deleted successfully");
    }

    // Get Farmers by District
    @GetMapping("/district/{district}")
    public ResponseEntity<List<FarmerResponseDTO>> getFarmersByDistrict(
            @PathVariable String district) {
        log.info("REST request to get farmers by district: {}", district);
        List<FarmerResponseDTO> response = farmerService.getFarmersByDistrict(district);
        return ResponseEntity.ok(response);
    }

    // Get Farmers by Village
    @GetMapping("/village/{village}")
    public ResponseEntity<List<FarmerResponseDTO>> getFarmersByVillage(
            @PathVariable String village) {
        log.info("REST request to get farmers by village: {}", village);
        List<FarmerResponseDTO> response = farmerService.getFarmersByVillage(village);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────
    // LAND ENDPOINTS
    // ─────────────────────────────────────────

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
        log.info("REST request to update land: {}", landId);
        LandResponseDTO response = farmerService.updateLand(landId, landRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Delete Land
    @DeleteMapping("/{farmerId}/lands/{landId}")
    public ResponseEntity<String> deleteLand(
            @PathVariable String farmerId,
            @PathVariable String landId) {
        log.info("REST request to delete land: {}", landId);
        farmerService.deleteLand(landId);
        return ResponseEntity.ok("Land deleted successfully");
    }
}

//**Key things to notice:**
//
//`@RequestMapping("/api/v1/farmers")` — notice the `v1` in URL. This is API versioning. Tomorrow if you change the API structure you create `v2` without breaking existing clients.
//
//`@Valid` — this triggers all the validations you wrote in `FarmerRequestDTO` like `@NotBlank`, `@Pattern`. Without `@Valid` the validations are ignored.
//
//`HttpStatus.CREATED` — for POST requests that create resources, always return `201 CREATED` not `200 OK`. This is REST standard.
//
//`ResponseEntity.ok()` — shortcut for `new ResponseEntity<>(body, HttpStatus.OK)`.
//
//Land URLs are nested under farmer — `/{farmerId}/lands` — this is proper REST design. Land belongs to farmer so URL reflects that relationship.
//
//---