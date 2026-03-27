package com.agriconnect.cropservice.service;

import com.agriconnect.cropservice.dto.*;
import java.util.List;

public interface CropService {

    // Crop operations
    CropResponseDTO addCrop(CropRequestDTO cropRequestDTO);
    CropResponseDTO getCropById(String cropId);
    List<CropResponseDTO> getCropsByFarmer(String farmerId);
    CropResponseDTO updateCropStatus(String cropId, CropStatusUpdateDTO statusUpdateDTO);
    void deleteCrop(String cropId);

    // Expense operations
    CropExpenseResponseDTO addExpense(String cropId, CropExpenseRequestDTO expenseRequestDTO);
    List<CropExpenseResponseDTO> getExpensesByCrop(String cropId);
    Double getTotalExpenses(String cropId);

    // Analytics
    Double calculateProfitLoss(String cropId);
}