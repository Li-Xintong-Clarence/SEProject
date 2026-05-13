package com.example.demo.service;

import com.example.demo.entity.Booking;
import java.util.List;
import java.util.Map;

/**
 * Booking Service Interface
 * Defines booking-related business operations
 */
public interface BookingService {
    /**
     * Query all bookings (admin)
     */
    List<Booking> findAll();
    /**
     * Query user's bookings
     */
    List<Booking> findByUserId(Long userId);
    /**
     * Query booking by ID
     */
    Booking findById(Long id);
    /**
     * Create booking by depot (auto-assign scooter)
     * @param userId User ID
     * @param depotId Depot ID (required)
     * @param hireOption Rental duration
     */
    Booking createByDepot(Long userId, Long depotId, String hireOption);
    /**
     * Create booking with specific scooter
     */
    boolean save(Booking booking);
    /**
     * Admin: Create booking for user (bypasses active booking check)
     * Used when admin creates booking on behalf of user
     */
    boolean adminSave(Booking booking);
    /**
     * Update booking info
     */
    boolean update(Booking booking);
    /**
     * Delete booking
     */
    boolean deleteById(Long id);
    /**
     * Extend booking
     * @param id Booking ID
     * @param hireOption Extension duration option: 1hr, 4hr, 1day, 1week
     */
    boolean extendBooking(Long id, String hireOption);
    /**
     * Cancel booking
     */
    boolean cancelBooking(Long id);
    /**
     * Return scooter (complete ride)
     * @param id Booking ID
     * @param endDepotId Depot ID where scooter is returned (optional, uses start depot if null)
     */
    boolean returnScooter(Long id, Long endDepotId);

    /**
     * Return scooter (complete ride) - backward compatible
     * Uses start depot as end depot
     */
    boolean returnScooter(Long id);
    /**
     * Pay booking
     */
    boolean payBooking(Long id);

    /**
     * Pay booking with enhanced security (tokenization and payment password)
     * @param id Booking ID
     * @param userId User ID
     * @param cardLast4 Card last 4 digits
     * @param amount Payment amount
     * @param paymentMethod Payment method
     * @param paymentPassword Payment password (optional)
     * @return Payment success
     */
    boolean payBooking(Long id, Long userId, String cardLast4, double amount, String paymentMethod, String paymentPassword);
    /**
     * Get user statistics
     */
    Map<String, Object> getUserStats(Long userId);
}
