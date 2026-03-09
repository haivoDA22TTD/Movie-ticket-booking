package com.cinema.controller;

import com.cinema.entity.Booking;
import com.cinema.entity.Showtime;
import com.cinema.entity.User;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.SeatRepository;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    
    @GetMapping("/showtime/{showtimeId}")
    public String selectSeats(
        @PathVariable Long showtimeId, 
        @AuthenticationPrincipal User user,
        Model model
    ) {
        try {
            log.info("Loading seat selection for showtime: {}, user: {}", showtimeId, user != null ? user.getEmail() : "null");
            
            // Nếu chưa đăng nhập, redirect về login
            if (user == null) {
                log.warn("User not authenticated, redirecting to login");
                return "redirect:/login?redirect=/booking/showtime/" + showtimeId;
            }
            
            Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));
            
            log.info("Found showtime: {}, screen: {}", showtime.getId(), showtime.getScreen().getId());
            
            var seats = seatRepository.findByScreenIdOrderBySeatRowAscSeatNumberAsc(
                showtime.getScreen().getId());
            
            log.info("Found {} seats for screen {}", seats.size(), showtime.getScreen().getId());
            
            var bookedSeats = bookingRepository.findByShowtimeId(showtimeId).stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .flatMap(b -> b.getSeats().stream())
                .map(s -> s.getId())
                .toList();
            
            log.info("Found {} booked seats", bookedSeats.size());
            
            model.addAttribute("showtime", showtime);
            model.addAttribute("seats", seats);
            model.addAttribute("bookedSeats", bookedSeats);
            return "seat-selection";
        } catch (Exception e) {
            log.error("Error loading seat selection: ", e);
            throw e;
        }
    }
    
    @PostMapping("/create")
    public String createBooking(
        @AuthenticationPrincipal User user,
        @RequestParam Long showtimeId,
        @RequestParam List<Long> seatIds,
        Model model
    ) {
        try {
            log.info("Creating booking for user: {}, showtime: {}, seats: {}", 
                user.getEmail(), showtimeId, seatIds);
            
            Booking booking = bookingService.createBooking(user, showtimeId, seatIds);
            
            log.info("Booking created successfully: {}", booking.getId());
            return "redirect:/booking/payment/" + booking.getId();
        } catch (Exception e) {
            log.error("Error creating booking: ", e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/booking/showtime/" + showtimeId + "?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/payment/{bookingId}")
    public String payment(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        return "payment";
    }
    
    @PostMapping("/confirm/{bookingId}")
    public String confirmPayment(@PathVariable Long bookingId) {
        bookingService.confirmBooking(bookingId);
        return "redirect:/booking/success/" + bookingId;
    }
    
    @GetMapping("/success/{bookingId}")
    public String bookingSuccess(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        return "booking-success";
    }
    
    @GetMapping("/my-bookings")
    public String myBookings(@AuthenticationPrincipal User user, Model model) {
        var bookings = bookingService.getUserBookings(user.getId());
        model.addAttribute("bookings", bookings);
        return "my-bookings";
    }
}
