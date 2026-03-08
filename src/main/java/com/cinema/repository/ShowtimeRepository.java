package com.cinema.repository;

import com.cinema.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :now ORDER BY s.startTime")
    List<Showtime> findUpcomingShowtimesByMovie(Long movieId, LocalDateTime now);
}
