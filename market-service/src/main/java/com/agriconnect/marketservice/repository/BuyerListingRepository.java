package com.agriconnect.marketservice.repository;

import com.agriconnect.marketservice.entity.BuyerListing;
import com.agriconnect.marketservice.entity.BuyerListing.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BuyerListingRepository
        extends JpaRepository<BuyerListing, String> {

    // Find active listings for a crop
    List<BuyerListing> findByCropNameAndListingStatus(
            String cropName, ListingStatus listingStatus);

    // Find active listings in a district
    List<BuyerListing> findByDistrictAndListingStatus(
            String district, ListingStatus listingStatus);

    // Find active listings for crop in district
    List<BuyerListing> findByCropNameAndDistrictAndListingStatus(
            String cropName,
            String district,
            ListingStatus listingStatus);

    // Find listings by buyer phone
    List<BuyerListing> findByBuyerPhone(String buyerPhone);

    // Find expired listings
    List<BuyerListing> findByValidUntilBeforeAndListingStatus(
            LocalDate date, ListingStatus listingStatus);

    // Find best price listings for a crop
    @Query("SELECT b FROM BuyerListing b " +
           "WHERE b.cropName = :cropName " +
           "AND b.listingStatus = 'ACTIVE' " +
           "ORDER BY b.offeredPricePerKg DESC")
    List<BuyerListing> findBestPriceListings(String cropName);

    // Find listings by crop and minimum price
    @Query("SELECT b FROM BuyerListing b " +
           "WHERE b.cropName = :cropName " +
           "AND b.district = :district " +
           "AND b.offeredPricePerKg >= :minPrice " +
           "AND b.listingStatus = 'ACTIVE' " +
           "ORDER BY b.offeredPricePerKg DESC")
    List<BuyerListing> findListingsByMinPrice(
            String cropName,
            String district,
            Double minPrice);
}