package com.example.demo;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Pricing;
import com.example.demo.entity.User;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.mapper.PricingMapper;
import com.example.demo.mapper.ScooterMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.EmailService;
import com.example.demo.service.ScooterService;
import com.example.demo.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private PricingMapper pricingMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ScooterMapper scooterMapper;

    @Mock
    private ScooterService scooterService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking dummyBooking;
    private Pricing dummyPricing;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        dummyBooking = new Booking();
        dummyBooking.setId(1L);
        dummyBooking.setUserId(100L);
        dummyBooking.setScooterId(200L);
        dummyBooking.setHireOption("1hr");
        dummyBooking.setTotalCost(new BigDecimal("5.00"));

        dummyPricing = new Pricing();
        dummyPricing.setId(1L);
        dummyPricing.setPrice(new BigDecimal("5.00"));

        dummyUser = new User();
        dummyUser.setId(100L);
        dummyUser.setUsername("testUser");
        dummyUser.setEmail("test@example.com");
    }

    @Test
    void testSaveBooking_Success() {
        when(pricingMapper.findById(1L)).thenReturn(dummyPricing);
        when(bookingMapper.insert(any(Booking.class))).thenReturn(1);

        boolean result = bookingService.save(dummyBooking);

        assertTrue(result);
        assertEquals("PENDING", dummyBooking.getStatus());
        assertNotNull(dummyBooking.getConfirmationCode());
        verify(bookingMapper, times(1)).insert(dummyBooking);
    }

    @Test
    void testPayBooking_Success() {
        dummyBooking.setStatus("PENDING");

        when(bookingMapper.findById(1L)).thenReturn(dummyBooking);
        when(userMapper.findById(anyLong())).thenReturn(dummyUser);
        when(scooterMapper.findById(200L)).thenReturn(null);
        when(bookingMapper.update(any(Booking.class))).thenReturn(1);

        boolean result = bookingService.payBooking(1L);

        assertTrue(result);
        assertEquals("PAID", dummyBooking.getStatus());
        assertNotNull(dummyBooking.getStartTime());
        assertNotNull(dummyBooking.getEndTime());
        verify(scooterService, times(1)).updateStatus(200L, "IN_USE");
        verify(emailService, times(1)).sendBookingConfirmation(
                anyString(), anyString(), any(), any(), anyString(), anyString(), anyString(), anyDouble()
        );
    }

    @Test
    void testExtendBooking_Success() {
        dummyBooking.setStatus("ACTIVE");
        dummyBooking.setEndTime(LocalDateTime.now().plusHours(1));

        when(bookingMapper.findById(1L)).thenReturn(dummyBooking);

        Pricing extendPricing = new Pricing();
        extendPricing.setPrice(new BigDecimal("15.00"));
        when(pricingMapper.findById(2L)).thenReturn(extendPricing);
        when(bookingMapper.update(any(Booking.class))).thenReturn(1);

        boolean result = bookingService.extendBooking(1L, "4hr");

        assertTrue(result);
        assertEquals(new BigDecimal("20.00"), dummyBooking.getTotalCost());
        verify(bookingMapper, times(1)).update(dummyBooking);
    }

    @Test
    void testCancelBooking_Success() {
        dummyBooking.setStatus("PENDING");

        when(bookingMapper.findById(1L)).thenReturn(dummyBooking);
        when(bookingMapper.update(any(Booking.class))).thenReturn(1);

        boolean result = bookingService.cancelBooking(1L);

        assertTrue(result);
        assertEquals("CANCELLED", dummyBooking.getStatus());
        verify(scooterService, times(1)).updateStatus(200L, "AVAILABLE");
    }
}
