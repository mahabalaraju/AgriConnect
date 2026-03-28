package com.agriconnect.marketservice.serviceimpl;

import com.agriconnect.marketservice.dto.*;
import com.agriconnect.marketservice.entity.BuyerListing;
import com.agriconnect.marketservice.entity.BuyerListing.ListingStatus;
import com.agriconnect.marketservice.entity.HarvestRecord;
import com.agriconnect.marketservice.entity.HarvestRecord.SellStatus;
import com.agriconnect.marketservice.entity.MarketPrice;
import com.agriconnect.marketservice.exception.BuyerListingNotFoundException;
import com.agriconnect.marketservice.exception.HarvestRecordNotFoundException;
import com.agriconnect.marketservice.exception.MarketPriceNotFoundException;
import com.agriconnect.marketservice.producer.MarketEventProducer;
import com.agriconnect.marketservice.repository.BuyerListingRepository;
import com.agriconnect.marketservice.repository.HarvestRecordRepository;
import com.agriconnect.marketservice.repository.MarketPriceRepository;
import com.agriconnect.marketservice.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketServiceImpl implements MarketService {

    private final MarketPriceRepository marketPriceRepository;
    private final BuyerListingRepository buyerListingRepository;
    private final HarvestRecordRepository harvestRecordRepository;
    private final MarketEventProducer marketEventProducer;

    // ─────────────────────────────────────────
    // MARKET PRICE OPERATIONS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public MarketPriceResponseDTO addMarketPrice(
            MarketPriceRequestDTO dto) {
        log.info("Adding market price for crop: {} in district: {}",
                dto.getCropName(), dto.getDistrict());

        MarketPrice price = MarketPrice.builder()
                .cropName(dto.getCropName())
                .cropType(dto.getCropType())
                .mandiName(dto.getMandiName())
                .district(dto.getDistrict())
                .state(dto.getState())
                .minPricePerKg(dto.getMinPricePerKg())
                .maxPricePerKg(dto.getMaxPricePerKg())
                .modalPricePerKg(dto.getModalPricePerKg())
                .priceDate(dto.getPriceDate())
                .priceTrend(dto.getPriceTrend())
                .build();

        MarketPrice savedPrice = marketPriceRepository.save(price);

        // Publish price.updated event
        marketEventProducer.publishPriceUpdated(savedPrice);

        log.info("Market price added: {}", savedPrice.getPriceId());
        return mapToPriceResponse(savedPrice);
    }

    @Override
    public MarketPriceResponseDTO getLatestPrice(
            String cropName, String district) {
        log.info("Fetching latest price for: {} in: {}",
                cropName, district);
        MarketPrice price = marketPriceRepository
                .findTopByCropNameAndDistrictOrderByPriceDateDesc(
                        cropName, district)
                .orElseThrow(() -> new MarketPriceNotFoundException(
                    "No price found for: " + cropName +
                    " in district: " + district));
        return mapToPriceResponse(price);
    }

    @Override
    public List<MarketPriceResponseDTO> getPricesByDistrict(
            String district) {
        return marketPriceRepository
                .findByDistrictOrderByPriceDateDesc(district)
                .stream()
                .map(this::mapToPriceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarketPriceResponseDTO> getPricesByCrop(String cropName) {
        return marketPriceRepository
                .findByCropNameOrderByPriceDateDesc(cropName)
                .stream()
                .map(this::mapToPriceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarketPriceResponseDTO> getPricesByCropAndDistrict(
            String cropName, String district) {
        return marketPriceRepository
                .findByCropNameAndDistrictOrderByPriceDateDesc(
                        cropName, district)
                .stream()
                .map(this::mapToPriceResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // BUYER LISTING OPERATIONS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public BuyerListingResponseDTO addBuyerListing(
            BuyerListingRequestDTO dto) {
        log.info("Adding buyer listing for crop: {} by: {}",
                dto.getCropName(), dto.getBuyerName());

        BuyerListing listing = BuyerListing.builder()
                .buyerName(dto.getBuyerName())
                .buyerPhone(dto.getBuyerPhone())
                .cropName(dto.getCropName())
                .cropType(dto.getCropType())
                .quantityRequiredKg(dto.getQuantityRequiredKg())
                .offeredPricePerKg(dto.getOfferedPricePerKg())
                .district(dto.getDistrict())
                .state(dto.getState())
                .validUntil(dto.getValidUntil())
                .build();

        BuyerListing savedListing = buyerListingRepository.save(listing);
        log.info("Buyer listing added: {}", savedListing.getListingId());
        return mapToListingResponse(savedListing);
    }

    @Override
    public List<BuyerListingResponseDTO> getActiveListingsByCrop(
            String cropName) {
        return buyerListingRepository
                .findByCropNameAndListingStatus(
                        cropName, ListingStatus.ACTIVE)
                .stream()
                .map(this::mapToListingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuyerListingResponseDTO> getActiveListingsByDistrict(
            String district) {
        return buyerListingRepository
                .findByDistrictAndListingStatus(
                        district, ListingStatus.ACTIVE)
                .stream()
                .map(this::mapToListingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuyerListingResponseDTO> getBestPriceListings(
            String cropName) {
        return buyerListingRepository
                .findBestPriceListings(cropName)
                .stream()
                .map(this::mapToListingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BuyerListingResponseDTO updateListingStatus(
            String listingId, String status) {
        BuyerListing listing = buyerListingRepository
                .findById(listingId)
                .orElseThrow(() -> new BuyerListingNotFoundException(
                    "Listing not found: " + listingId));
        listing.setListingStatus(ListingStatus.valueOf(status));
        return mapToListingResponse(buyerListingRepository.save(listing));
    }

    // ─────────────────────────────────────────
    // HARVEST RECORD OPERATIONS
    // ─────────────────────────────────────────

    @Override
    public HarvestRecordResponseDTO getHarvestRecord(String cropId) {
        HarvestRecord record = harvestRecordRepository
                .findByCropId(cropId)
                .orElseThrow(() -> new HarvestRecordNotFoundException(
                    "Harvest record not found for cropId: " + cropId));
        return mapToHarvestResponse(record);
    }

    @Override
    public List<HarvestRecordResponseDTO> getHarvestsByFarmer(
            String farmerId) {
        return harvestRecordRepository
                .findByFarmerId(farmerId)
                .stream()
                .map(this::mapToHarvestResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HarvestRecordResponseDTO updateSellStatus(
            String recordId, String sellStatus) {
        HarvestRecord record = harvestRecordRepository
                .findById(recordId)
                .orElseThrow(() -> new HarvestRecordNotFoundException(
                    "Harvest record not found: " + recordId));
        record.setSellStatus(SellStatus.valueOf(sellStatus));
        return mapToHarvestResponse(
                harvestRecordRepository.save(record));
    }

    // ─────────────────────────────────────────
    // ANALYTICS
    // ─────────────────────────────────────────

    @Override
    public Double getAveragePrice(String cropName, String district) {
        Double avg = marketPriceRepository.getAverageModalPrice(
                cropName, district,
                LocalDate.now().minusDays(7));
        return avg != null ? avg : 0.0;
    }

    @Override
    public Double getSuggestedPrice(String cropName, String district) {
        log.info("Calculating suggested price for: {} in: {}",
                cropName, district);

        // Get 7 day average
        Double avgPrice = getAveragePrice(cropName, district);

        if (avgPrice == 0.0) {
            log.warn("No price data found for: {} in: {}",
                    cropName, district);
            return 0.0;
        }

        // Suggest 5% above average — farmer gets better than market
        Double suggestedPrice = avgPrice * 1.05;

        log.info("Suggested price for {}: Rs.{}/kg", cropName,
                suggestedPrice);
        return suggestedPrice;
    }

    @Override
    public Double getTotalAvailableYield(String cropName) {
        Double total = harvestRecordRepository
                .getTotalAvailableYield(cropName);
        return total != null ? total : 0.0;
    }

    // ─────────────────────────────────────────
    // PROCESS HARVEST EVENT (called by Kafka consumer)
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public void processHarvestEvent(CropHarvestedEvent event) {
        log.info("Processing harvest event for cropID: {}",
                event.getCropId());

        // Check if already processed (idempotency)
        if (harvestRecordRepository.findByCropId(
                event.getCropId()).isPresent()) {
            log.warn("Harvest record already exists for cropID: {} " +
                     "- skipping", event.getCropId());
            return;
        }

        // Get suggested price for this crop
        Double suggestedPrice = getSuggestedPrice(
                event.getCropName(), "Karnataka");

        // If no market data use cost plus pricing
        if (suggestedPrice == 0.0 && event.getTotalExpenses() != null
                && event.getActualYieldKg() != null
                && event.getActualYieldKg() > 0) {
            // Cost per kg + 30% profit margin
            suggestedPrice = (event.getTotalExpenses() /
                    event.getActualYieldKg()) * 1.30;
            log.info("Using cost-plus pricing: Rs.{}/kg", suggestedPrice);
        }

        // Create harvest record
        HarvestRecord record = HarvestRecord.builder()
                .cropId(event.getCropId())
                .farmerId(event.getFarmerId())
                .cropName(event.getCropName())
                .cropType(event.getCropType())
                .actualYieldKg(event.getActualYieldKg())
                .expectedYieldKg(event.getExpectedYieldKg())
                .totalExpenses(event.getTotalExpenses())
                .suggestedPricePerKg(suggestedPrice)
                .harvestDate(event.getActualHarvestDate())
                .build();

        harvestRecordRepository.save(record);
        log.info("Harvest record created for cropID: {} | " +
                 "Suggested price: Rs.{}/kg",
                event.getCropId(), suggestedPrice);
    }

    // ─────────────────────────────────────────
    // MAPPING METHODS
    // ─────────────────────────────────────────

    private MarketPriceResponseDTO mapToPriceResponse(MarketPrice price) {
        return MarketPriceResponseDTO.builder()
                .priceId(price.getPriceId())
                .cropName(price.getCropName())
                .cropType(price.getCropType())
                .mandiName(price.getMandiName())
                .district(price.getDistrict())
                .state(price.getState())
                .minPricePerKg(price.getMinPricePerKg())
                .maxPricePerKg(price.getMaxPricePerKg())
                .modalPricePerKg(price.getModalPricePerKg())
                .priceDate(price.getPriceDate())
                .priceTrend(price.getPriceTrend())
                .createdAt(price.getCreatedAt())
                .build();
    }

    private BuyerListingResponseDTO mapToListingResponse(
            BuyerListing listing) {
        return BuyerListingResponseDTO.builder()
                .listingId(listing.getListingId())
                .buyerName(listing.getBuyerName())
                .buyerPhone(listing.getBuyerPhone())
                .cropName(listing.getCropName())
                .cropType(listing.getCropType())
                .quantityRequiredKg(listing.getQuantityRequiredKg())
                .offeredPricePerKg(listing.getOfferedPricePerKg())
                .district(listing.getDistrict())
                .state(listing.getState())
                .validUntil(listing.getValidUntil())
                .listingStatus(listing.getListingStatus())
                .createdAt(listing.getCreatedAt())
                .build();
    }

    private HarvestRecordResponseDTO mapToHarvestResponse(
            HarvestRecord record) {
        return HarvestRecordResponseDTO.builder()
                .recordId(record.getRecordId())
                .cropId(record.getCropId())
                .farmerId(record.getFarmerId())
                .cropName(record.getCropName())
                .cropType(record.getCropType())
                .actualYieldKg(record.getActualYieldKg())
                .expectedYieldKg(record.getExpectedYieldKg())
                .totalExpenses(record.getTotalExpenses())
                .suggestedPricePerKg(record.getSuggestedPricePerKg())
                .harvestDate(record.getHarvestDate())
                .sellStatus(record.getSellStatus())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
//```
//
//---
//
//**Key things to notice:**
//
//`getSuggestedPrice` adds 5% above average — farmer always gets slightly better than market rate. Simple but farmer-friendly business logic.
//
//**Cost-plus pricing fallback** — if no mandi data exists for that crop, we calculate price as `(totalExpenses / actualYieldKg) * 1.30`. This gives farmer 30% profit margin on top of their costs. Smart fallback!
//
//`processHarvestEvent` idempotency check — if same `crop.harvested` event arrives twice due to Kafka retry, we skip the second one. Harvest record is created only once.
//
//`updateSellStatus` — when farmer sells their produce, status moves from `AVAILABLE` to `PARTIALLY_SOLD` or `SOLD`. Buyers can see real-time availability.
//
//---
//
//**Your service layer:**
//```
//service/
//└── MarketService.java              ✅
//
//serviceimpl/
//└── MarketServiceImpl.java          ✅