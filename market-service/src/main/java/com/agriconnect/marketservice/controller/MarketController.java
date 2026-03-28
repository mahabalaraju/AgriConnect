package com.agriconnect.marketservice.controller;

import com.agriconnect.marketservice.dto.*;
import com.agriconnect.marketservice.service.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
@Slf4j
public class MarketController {

    private final MarketService marketService;

    // ─────────────────────────────────────────
    // MARKET PRICE ENDPOINTS
    // ─────────────────────────────────────────

    // Add Market Price
    @PostMapping("/prices")
    public ResponseEntity<MarketPriceResponseDTO> addMarketPrice(
            @Valid @RequestBody MarketPriceRequestDTO requestDTO) {
        log.info("REST request to add market price for: {}",
                requestDTO.getCropName());
        MarketPriceResponseDTO response =
                marketService.addMarketPrice(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get Latest Price for crop in district
    @GetMapping("/prices/latest")
    public ResponseEntity<MarketPriceResponseDTO> getLatestPrice(
            @RequestParam String cropName,
            @RequestParam String district) {
        log.info("REST request to get latest price for: {} in: {}",
                cropName, district);
        MarketPriceResponseDTO response =
                marketService.getLatestPrice(cropName, district);
        return ResponseEntity.ok(response);
    }

    // Get All Prices by District
    @GetMapping("/prices/district/{district}")
    public ResponseEntity<List<MarketPriceResponseDTO>> getPricesByDistrict(
            @PathVariable String district) {
        log.info("REST request to get prices for district: {}", district);
        List<MarketPriceResponseDTO> response =
                marketService.getPricesByDistrict(district);
        return ResponseEntity.ok(response);
    }

    // Get All Prices by Crop
    @GetMapping("/prices/crop/{cropName}")
    public ResponseEntity<List<MarketPriceResponseDTO>> getPricesByCrop(
            @PathVariable String cropName) {
        log.info("REST request to get prices for crop: {}", cropName);
        List<MarketPriceResponseDTO> response =
                marketService.getPricesByCrop(cropName);
        return ResponseEntity.ok(response);
    }

    // Get Prices by Crop and District
    @GetMapping("/prices/crop/{cropName}/district/{district}")
    public ResponseEntity<List<MarketPriceResponseDTO>> getPricesByCropAndDistrict(
            @PathVariable String cropName,
            @PathVariable String district) {
        log.info("REST request to get prices for: {} in: {}",
                cropName, district);
        List<MarketPriceResponseDTO> response =
                marketService.getPricesByCropAndDistrict(
                        cropName, district);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────
    // BUYER LISTING ENDPOINTS
    // ─────────────────────────────────────────

    // Add Buyer Listing
    @PostMapping("/listings")
    public ResponseEntity<BuyerListingResponseDTO> addBuyerListing(
            @Valid @RequestBody BuyerListingRequestDTO requestDTO) {
        log.info("REST request to add buyer listing for: {}",
                requestDTO.getCropName());
        BuyerListingResponseDTO response =
                marketService.addBuyerListing(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get Active Listings by Crop
    @GetMapping("/listings/crop/{cropName}")
    public ResponseEntity<List<BuyerListingResponseDTO>> getActiveListingsByCrop(
            @PathVariable String cropName) {
        log.info("REST request to get listings for crop: {}", cropName);
        List<BuyerListingResponseDTO> response =
                marketService.getActiveListingsByCrop(cropName);
        return ResponseEntity.ok(response);
    }

    // Get Active Listings by District
    @GetMapping("/listings/district/{district}")
    public ResponseEntity<List<BuyerListingResponseDTO>> getActiveListingsByDistrict(
            @PathVariable String district) {
        log.info("REST request to get listings for district: {}",
                district);
        List<BuyerListingResponseDTO> response =
                marketService.getActiveListingsByDistrict(district);
        return ResponseEntity.ok(response);
    }

    // Get Best Price Listings for Crop
    @GetMapping("/listings/best-price/{cropName}")
    public ResponseEntity<List<BuyerListingResponseDTO>> getBestPriceListings(
            @PathVariable String cropName) {
        log.info("REST request to get best price listings for: {}",
                cropName);
        List<BuyerListingResponseDTO> response =
                marketService.getBestPriceListings(cropName);
        return ResponseEntity.ok(response);
    }

    // Update Listing Status
    @PatchMapping("/listings/{listingId}/status")
    public ResponseEntity<BuyerListingResponseDTO> updateListingStatus(
            @PathVariable String listingId,
            @RequestParam String status) {
        log.info("REST request to update listing status: {} to {}",
                listingId, status);
        BuyerListingResponseDTO response =
                marketService.updateListingStatus(listingId, status);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────
    // HARVEST RECORD ENDPOINTS
    // ─────────────────────────────────────────

    // Get Harvest Record by CropId
    @GetMapping("/harvests/crop/{cropId}")
    public ResponseEntity<HarvestRecordResponseDTO> getHarvestRecord(
            @PathVariable String cropId) {
        log.info("REST request to get harvest record for cropID: {}",
                cropId);
        HarvestRecordResponseDTO response =
                marketService.getHarvestRecord(cropId);
        return ResponseEntity.ok(response);
    }

    // Get All Harvests by Farmer
    @GetMapping("/harvests/farmer/{farmerId}")
    public ResponseEntity<List<HarvestRecordResponseDTO>> getHarvestsByFarmer(
            @PathVariable String farmerId) {
        log.info("REST request to get harvests for farmerID: {}",
                farmerId);
        List<HarvestRecordResponseDTO> response =
                marketService.getHarvestsByFarmer(farmerId);
        return ResponseEntity.ok(response);
    }

    // Update Sell Status
    @PatchMapping("/harvests/{recordId}/sell-status")
    public ResponseEntity<HarvestRecordResponseDTO> updateSellStatus(
            @PathVariable String recordId,
            @RequestParam String sellStatus) {
        log.info("REST request to update sell status: {} to {}",
                recordId, sellStatus);
        HarvestRecordResponseDTO response =
                marketService.updateSellStatus(recordId, sellStatus);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────
    // ANALYTICS ENDPOINTS
    // ─────────────────────────────────────────

    // Get Average Price (7 day)
    @GetMapping("/analytics/average-price")
    public ResponseEntity<Double> getAveragePrice(
            @RequestParam String cropName,
            @RequestParam String district) {
        log.info("REST request to get average price for: {} in: {}",
                cropName, district);
        Double response = marketService.getAveragePrice(
                cropName, district);
        return ResponseEntity.ok(response);
    }

    // Get Suggested Selling Price
    @GetMapping("/analytics/suggested-price")
    public ResponseEntity<Double> getSuggestedPrice(
            @RequestParam String cropName,
            @RequestParam String district) {
        log.info("REST request to get suggested price for: {} in: {}",
                cropName, district);
        Double response = marketService.getSuggestedPrice(
                cropName, district);
        return ResponseEntity.ok(response);
    }

    // Get Total Available Yield
    @GetMapping("/analytics/available-yield/{cropName}")
    public ResponseEntity<Double> getTotalAvailableYield(
            @PathVariable String cropName) {
        log.info("REST request to get available yield for: {}",
                cropName);
        Double response = marketService.getTotalAvailableYield(cropName);
        return ResponseEntity.ok(response);
    }
}