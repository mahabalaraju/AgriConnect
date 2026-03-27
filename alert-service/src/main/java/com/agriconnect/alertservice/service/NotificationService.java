package com.agriconnect.alertservice.service;

import com.agriconnect.alertservice.dto.FarmerRegisteredEvent;

public interface NotificationService {

    void sendWelcomeNotification(FarmerRegisteredEvent event);
    void sendSmsNotification(String phoneNumber, String message);
    void sendPushNotification(String farmerId, String message);
}