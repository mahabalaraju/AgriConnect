package com.agriconnect.marketservice.service;

import com.agriconnect.marketservice.dto.*;
import com.agriconnect.marketservice.entity.HarvestRecord;
import java.util.List;

public interface MarketService {

    // Market Price operations
    MarketPriceResponseDTO addMarketPrice(MarketPriceRequestDTO requestDTO);
    MarketPriceResponseDTO getLatestPrice(String cropName, String district);
    List<MarketPriceResponseDTO> getPricesByDistrict(String district);
    List<MarketPriceResponseDTO> getPricesByCrop(String cropName);
    List<MarketPriceResponseDTO> getPricesByCropAndDistrict(
            String cropName, String district);

    // Buyer Listing operations
    BuyerListingResponseDTO addBuyerListing(
            BuyerListingRequestDTO requestDTO);
    List<BuyerListingResponseDTO> getActiveListingsByCrop(String cropName);
    List<BuyerListingResponseDTO> getActiveListingsByDistrict(
            String district);
    List<BuyerListingResponseDTO> getBestPriceListings(String cropName);
    BuyerListingResponseDTO updateListingStatus(
            String listingId, String status);

    // Harvest Record operations
    HarvestRecordResponseDTO getHarvestRecord(String cropId);
    List<HarvestRecordResponseDTO> getHarvestsByFarmer(String farmerId);
    HarvestRecordResponseDTO updateSellStatus(
            String recordId, String sellStatus);

    // Analytics
    Double getAveragePrice(String cropName, String district);
    Double getSuggestedPrice(String cropName, String district);
    Double getTotalAvailableYield(String cropName);

    // Internal - called by Kafka consumer
    void processHarvestEvent(
            com.agriconnect.marketservice.dto.CropHarvestedEvent event);
}