package com.agriconnect.marketservice.repository;

import com.agriconnect.marketservice.entity.MarketPrice;
import com.agriconnect.marketservice.entity.MarketPrice.PriceTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceRepository 
        extends JpaRepository<MarketPrice, String> {

    // Find latest price for a crop in a district
    Optional<MarketPrice> findTopByCropNameAndDistrictOrderByPriceDateDesc(
            String cropName, String district);

    // Find all prices for a crop
    List<MarketPrice> findByCropNameOrderByPriceDateDesc(String cropName);

    // Find all prices in a district
    List<MarketPrice> findByDistrictOrderByPriceDateDesc(String district);

    // Find prices for a crop in a district
    List<MarketPrice> findByCropNameAndDistrictOrderByPriceDateDesc(
            String cropName, String district);

    // Find prices by trend
    List<MarketPrice> findByPriceTrend(PriceTrend priceTrend);

    // Find prices between dates
    List<MarketPrice> findByCropNameAndPriceDateBetween(
            String cropName,
            LocalDate startDate,
            LocalDate endDate);

    // Find rising price crops in a district
    List<MarketPrice> findByDistrictAndPriceTrend(
            String district, PriceTrend priceTrend);

    // Get average modal price for a crop in last 7 days
    @Query("SELECT AVG(m.modalPricePerKg) FROM MarketPrice m " +
           "WHERE m.cropName = :cropName " +
           "AND m.district = :district " +
           "AND m.priceDate >= :fromDate")
    Double getAverageModalPrice(
            String cropName,
            String district,
            LocalDate fromDate);
}