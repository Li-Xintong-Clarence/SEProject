package com.example.demo.service;

/**
 * Email Service Interface
 * Provides email sending related functions
 */
public interface EmailService {
    /**
     * Send booking confirmation email
     * @param toEmail Recipient email
     * @param username Username
     * @param confirmationCode Booking confirmation code
     * @param scooterNumber Scooter number
     * @param hireOption Rental option
     * @param startTime Start time
     * @param endTime End time
     * @param totalCost Total fee
     */
    void sendBookingConfirmation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption, String startTime, String endTime, double totalCost);

    /**
     * Send booking cancellation confirmation
     */
    void sendBookingCancellation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption);

    /**
     * Send ride completion notification
     */
    void sendRideCompletion(String toEmail, String username, String confirmationCode,
                           String scooterNumber, String startTime, String endTime, double totalCost);

    /**
     * Send auto-return notification email
     * @param toEmail Recipient email
     * @param username Username
     * @param confirmationCode Booking confirmation code
     * @param scooterNumber Scooter number
     * @param startTime Start time
     * @param endTime End time
     * @param depotName Return depot name
     * @param totalCost Total fee
     */
    void sendAutoReturnNotification(String toEmail, String username, String confirmationCode,
                                    String scooterNumber, String startTime, String endTime,
                                    String depotName, double totalCost);
}
