package com.agriconnect.marketservice.repository;

import com.agriconnect.marketservice.entity.HarvestRecord;
import com.agriconnect.marketservice.entity.HarvestRecord.SellStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HarvestRecordRepository
        extends JpaRepository<HarvestRecord, String> {

    // Find harvest record by cropId
    Optional<HarvestRecord> findByCropId(String cropId);

    // Find all harvest records for a farmer
    List<HarvestRecord> findByFarmerId(String farmerId);

    // Find available harvests for a crop
    List<HarvestRecord> findByCropNameAndSellStatus(
            String cropName, SellStatus sellStatus);

    // Find available harvests in a district
    // (joined with farmer district - simplified here)
    List<HarvestRecord> findBySellStatus(SellStatus sellStatus);

    // Total yield available for a crop
    @Query("SELECT SUM(h.actualYieldKg) FROM HarvestRecord h " +
           "WHERE h.cropName = :cropName " +
           "AND h.sellStatus = 'AVAILABLE'")
    Double getTotalAvailableYield(String cropName);

    // Find harvest records by farmer and sell status
    List<HarvestRecord> findByFarmerIdAndSellStatus(
            String farmerId, SellStatus sellStatus);
}
//```
//
//---
//
//**Key things to notice:**
//
//`findTopByCropNameAndDistrictOrderByPriceDateDesc` — finds the **latest** price for a crop in a district. The `Top` keyword in Spring Data JPA means fetch only one record — the most recent one. This is what we use to suggest price to farmer.
//
//`getAverageModalPrice` for last 7 days — this gives a **7-day moving average price**. Much more reliable than single day price. Farmers can see if prices are consistently rising or just a one-day spike.
//
//`findBestPriceListings` — orders buyer listings by highest offered price. Farmer sees the best deal at the top. Simple but powerful for farmers.
//
//`findListingsByMinPrice` — farmer can say "show me buyers offering at least Rs.25/kg for Rice in Mandya". This is smart filtering that saves farmer time.
//
//`getTotalAvailableYield` — shows buyers how much stock is available for a crop across all farmers. Buyer can decide if supply is enough for their requirement.
//
//---
//
//**Your repository layer:**
//```
//repository/
//├── MarketPriceRepository.java      ✅
//├── BuyerListingRepository.java     ✅
//└── HarvestRecordRepository.java    ✅