package com.cinema.repository;

import com.cinema.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByShowtimeId(Long showtimeId);
    
    // New methods for admin statistics
    List<Booking> findByStatus(Booking.BookingStatus status);
    List<Booking> findByStatusAndCreatedAtAfter(Booking.BookingStatus status, LocalDateTime createdAt);
    List<Booking> findTop5ByOrderByCreatedAtDesc();
}
