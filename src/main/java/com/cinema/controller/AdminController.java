package com.cinema.controller;

import com.cinema.entity.Booking;
import com.cinema.entity.Cinema;
import com.cinema.entity.Screen;
import com.cinema.entity.Showtime;
import com.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final CinemaRepository cinemaRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public String adminDashboard(Model model) {
        // Existing counts
        model.addAttribute("cinemaCount", cinemaRepository.count());
        model.addAttribute("screenCount", screenRepository.count());
        model.addAttribute("showtimeCount", showtimeRepository.count());
        
        // New statistics
        long totalCustomers = userRepository.count();
        model.addAttribute("totalCustomers", totalCustomers);
        
        // Calculate total revenue from confirmed bookings
        List<Booking> confirmedBookings = bookingRepository.findByStatus(Booking.BookingStatus.CONFIRMED);
        double totalRevenue = confirmedBookings.stream()
                .mapToDouble(booking -> booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0)
                .sum();
        
        // Format revenue for display
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        model.addAttribute("totalRevenue", currencyFormat.format(totalRevenue));
        model.addAttribute("totalRevenueRaw", totalRevenue);
        
        // Monthly statistics
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Booking> monthlyBookings = bookingRepository.findByStatusAndCreatedAtAfter(
                Booking.BookingStatus.CONFIRMED, startOfMonth);
        
        double monthlyRevenue = monthlyBookings.stream()
                .mapToDouble(booking -> booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0)
                .sum();
        
        model.addAttribute("monthlyRevenue", currencyFormat.format(monthlyRevenue));
        model.addAttribute("monthlyBookings", monthlyBookings.size());
        
        // Recent bookings for activity feed
        List<Booking> recentBookings = bookingRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("recentBookings", recentBookings);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/cinemas")
    public String listCinemas(Model model) {
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "admin/cinemas";
    }
    
    @GetMapping("/screens")
    public String listScreens(Model model) {
        model.addAttribute("screens", screenRepository.findAll());
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "admin/screens";
    }
    
    @GetMapping("/showtimes")
    public String listShowtimes(Model model) {
        model.addAttribute("showtimes", showtimeRepository.findAll());
        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("screens", screenRepository.findAll());
        return "admin/showtimes";
    }
}
