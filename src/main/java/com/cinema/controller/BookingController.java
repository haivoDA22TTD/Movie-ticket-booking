package com.cinema.controller;

import com.cinema.entity.Booking;
import com.cinema.entity.Showtime;
import com.cinema.entity.User;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.SeatRepository;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    
    @GetMapping("/showtime/{showtimeId}")
    public String selectSeats(@PathVariable Long showtimeId, Model model) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new RuntimeException("Showtime not found"));
        
        var seats = seatRepository.findByScreenIdOrderBySeatRowAscSeatNumberAsc(
            showtime.getScreen().getId());
        
        var bookedSeats = bookingRepository.findByShowtimeId(showtimeId).stream()
            .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
            .flatMap(b -> b.getSeats().stream())
            .map(s -> s.getId())
            .toList();
        
        model.addAttribute("showtime", showtime);
        model.addAttribute("seats", seats);
        model.addAttribute("bookedSeats", bookedSeats);
        return "seat-selection";
    }
    
    @PostMapping("/create")
    public String createBooking(
        @AuthenticationPrincipal User user,
        @RequestParam Long showtimeId,
        @RequestParam List<Long> seatIds,
        Model model
    ) {
        Booking booking = bookingService.createBooking(user, showtimeId, seatIds);
        return "redirect:/booking/payment/" + booking.getId();
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
