package com.agriconnect.cropservice.repository;

import com.agriconnect.cropservice.entity.CropExpense;
import com.agriconnect.cropservice.entity.CropExpense.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CropExpenseRepository extends JpaRepository<CropExpense, String> {

    // Find all expenses of a crop
    List<CropExpense> findByCrop_CropId(String cropId);

    // Find expenses by type
    List<CropExpense> findByCrop_CropIdAndExpenseType(
            String cropId, ExpenseType expenseType);

    // Calculate total expenses for a crop
    @Query("SELECT SUM(e.amount) FROM CropExpense e " +
           "WHERE e.crop.cropId = :cropId")
    Double calculateTotalExpenses(String cropId);

    // Calculate expenses by type for a crop
    @Query("SELECT SUM(e.amount) FROM CropExpense e " +
           "WHERE e.crop.cropId = :cropId " +
           "AND e.expenseType = :expenseType")
    Double calculateExpensesByType(String cropId, ExpenseType expenseType);
}

//
//**Key things to notice:**
//
//`findCropsReadyToHarvest` — this is a powerful query. Later you can run a **scheduled job** every morning that calls this method and automatically notifies farmers whose crops are ready to harvest. That's real AgriTech value!
//
//`calculateTotalExpenses` — this aggregation query gives total money spent on a crop. When combined with `actualYieldKg` and market price, you can calculate **profit or loss** for each farmer. That's your premium analytics feature.
//
//`findByCrop_CropId` — underscore navigation to access nested object's field, same pattern as farmer-service.
//
//`calculateExpensesByType` — farmer can see exactly how much they spent on seeds vs fertilizer vs labour. Very useful insight.
//
//---
//
//**Your repository layer:**
//```
//repository/
//├── CropRepository.java         ✅
//└── CropExpenseRepository.java  ✅