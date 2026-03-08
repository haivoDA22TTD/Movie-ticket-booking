package com.cinema.service;

import com.cinema.entity.Booking;
import com.cinema.entity.Seat;
import com.cinema.entity.Showtime;
import com.cinema.entity.User;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.SeatRepository;
import com.cinema.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final QRCodeService qrCodeService;
    
    @Transactional
    public Booking createBooking(User user, Long showtimeId, List<Long> seatIds) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new RuntimeException("Showtime not found"));
        
        List<Seat> seats = seatRepository.findAllById(seatIds);
        
        // Check if seats are already booked
        List<Booking> existingBookings = bookingRepository.findByShowtimeId(showtimeId);
        List<Long> bookedSeatIds = existingBookings.stream()
            .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
            .flatMap(b -> b.getSeats().stream())
            .map(Seat::getId)
            .toList();
        
        boolean hasConflict = seatIds.stream().anyMatch(bookedSeatIds::contains);
        if (hasConflict) {
            throw new RuntimeException("Some seats are already booked");
        }
        
        // Calculate total price
        double totalPrice = seats.stream()
            .mapToDouble(seat -> {
                double basePrice = showtime.getPrice();
                return switch (seat.getType()) {
                    case VIP -> basePrice * 1.5;
                    case COUPLE -> basePrice * 2.0;
                    default -> basePrice;
                };
            })
            .sum();
        
        String bookingCode = generateBookingCode();
        
        Booking booking = Booking.builder()
            .bookingCode(bookingCode)
            .user(user)
            .showtime(showtime)
            .seats(seats)
            .totalPrice(totalPrice)
            .status(Booking.BookingStatus.PENDING)
            .build();
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaidAt(LocalDateTime.now());
        
        // Generate QR code
        String qrData = String.format("BOOKING:%s|MOVIE:%s|TIME:%s|SEATS:%s",
            booking.getBookingCode(),
            booking.getShowtime().getMovie().getTitle(),
            booking.getShowtime().getStartTime(),
            booking.getSeats().stream()
                .map(s -> s.getSeatRow() + s.getSeatNumber())
                .reduce((a, b) -> a + "," + b)
                .orElse("")
        );
        
        String qrCode = qrCodeService.generateQRCode(qrData);
        booking.setQrCode(qrCode);
        
        return bookingRepository.save(booking);
    }
    
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Booking getBookingByCode(String bookingCode) {
        return bookingRepository.findByBookingCode(bookingCode)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
    
    private String generateBookingCode() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
