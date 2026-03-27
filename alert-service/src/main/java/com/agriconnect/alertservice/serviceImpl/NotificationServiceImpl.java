package com.agriconnect.alertservice.serviceImpl;

import com.agriconnect.alertservice.dto.FarmerRegisteredEvent;
import com.agriconnect.alertservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendWelcomeNotification(FarmerRegisteredEvent event) {
        log.info("Sending welcome notification to farmer: {}", event.getFullName());

        String message = buildWelcomeMessage(event);

        // Send SMS
        sendSmsNotification(event.getPhoneNumber(), message);

        // Send Push Notification
        sendPushNotification(event.getFarmerId(), message);

        log.info("Welcome notification sent successfully to: {}", 
                event.getPhoneNumber());
    }

    @Override
    public void sendSmsNotification(String phoneNumber, String message) {
        // In real world: integrate Twilio or MSG91 SMS API here
        log.info("SMS sent to: {} | Message: {}", phoneNumber, message);
    }

    @Override
    public void sendPushNotification(String farmerId, String message) {
        // In real world: integrate Firebase FCM here
        log.info("Push notification sent to farmerID: {} | Message: {}", 
                farmerId, message);
    }

    private String buildWelcomeMessage(FarmerRegisteredEvent event) {
        return String.format(
            "Welcome to AgriConnect, %s! " +
            "Your farm of %.1f acres in %s, %s is now registered. " +
            "Connect with buyers and get best prices for your crops!",
            event.getFullName(),
            event.getTotalLandAcres(),
            event.getVillage(),
            event.getDistrict()
        );
    }
}