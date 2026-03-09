package com.cinema.service;

import com.cinema.dto.TMDBMovieResponse;
import com.cinema.entity.*;
import com.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataInitService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CinemaRepository cinemaRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TMDBService tmdbService;
    
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("🚀 Starting data initialization (async)...");
        
        try {
            // Create admin account
            if (!userRepository.existsByEmail("admin@cinema.vn")) {
                log.info("Creating admin account...");
                User admin = User.builder()
                    .email("admin@cinema.vn")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .phone("0000000000")
                    .role(User.Role.ADMIN)
                    .enabled(true)
                    .build();
                userRepository.save(admin);
                log.info("✅ Admin account created: admin@cinema.vn / admin123");
            }
            
            // Sync movies from TMDB to database
            if (movieRepository.count() == 0) {
                log.info("Syncing 5 movies from TMDB to database...");
                try {
                    List<TMDBMovieResponse> nowPlaying = tmdbService.getNowPlayingMovies();
                    
                    // Chỉ lấy 5 phim đầu để tránh timeout
                    for (int i = 0; i < Math.min(5, nowPlaying.size()); i++) {
                        TMDBMovieResponse tmdbMovie = nowPlaying.get(i);
                        Movie movie = Movie.builder()
                            .tmdbId(tmdbMovie.getId())
                            .title(tmdbMovie.getTitle())
                            .overview(tmdbMovie.getOverview())
                            .posterPath(tmdbMovie.getPosterPath())
                            .backdropPath(tmdbMovie.getBackdropPath())
                            .releaseDate(tmdbMovie.getReleaseDate() != null ? 
                                LocalDate.parse(tmdbMovie.getReleaseDate()) : null)
                            .voteAverage(tmdbMovie.getVoteAverage())
                            .status(Movie.MovieStatus.NOW_SHOWING)
                            .build();
                        
                        if (tmdbMovie.getGenres() != null) {
                            movie.setGenres(tmdbMovie.getGenres().stream()
                                .map(TMDBMovieResponse.Genre::getName)
                                .collect(Collectors.toList()));
                        }
                        
                        movieRepository.save(movie);
                    }
                    log.info("✅ Synced 5 movies from TMDB");
                } catch (Exception e) {
                    log.error("Failed to sync movies: {}", e.getMessage());
                }
            }
            
            // Create sample data if not exists
            if (cinemaRepository.count() == 0) {
                log.info("Creating sample cinema data...");
                
                // Create Cinema
                Cinema cinema = Cinema.builder()
                    .name("CGV Vincom")
                    .address("191 Bà Triệu, Hai Bà Trưng, Hà Nội")
                    .phone("1900xxxx")
                    .build();
                cinema = cinemaRepository.save(cinema);
                log.info("✅ Created cinema: {}", cinema.getName());
                
                // Create Screens
                for (int i = 1; i <= 3; i++) {
                    Screen screen = Screen.builder()
                        .name("Phòng " + i)
                        .cinema(cinema)
                        .totalSeats(96)
                        .build();
                    screen = screenRepository.save(screen);
                    log.info("✅ Created screen: {}", screen.getName());
                    
                    // Create Seats (8 rows x 12 seats)
                    List<Seat> seats = new ArrayList<>();
                    String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H"};
                    for (String row : rows) {
                        for (int num = 1; num <= 12; num++) {
                            Seat seat = Seat.builder()
                                .screen(screen)
                                .seatRow(row)
                                .seatNumber(num)
                                .type(Seat.SeatType.STANDARD)
                                .build();
                            seats.add(seat);
                        }
                    }
                    seatRepository.saveAll(seats);
                    log.info("✅ Created {} seats for {}", seats.size(), screen.getName());
                }
                
                log.info("✅ Sample data created successfully!");
            }
            
            // Create showtimes for movies
            if (showtimeRepository.count() == 0 && movieRepository.count() > 0) {
                log.info("Creating sample showtimes...");
                
                List<Movie> movies = movieRepository.findAll();
                List<Screen> screens = screenRepository.findAll();
                
                if (!movies.isEmpty() && !screens.isEmpty()) {
                    // Create showtimes for first 5 movies
                    for (int i = 0; i < Math.min(5, movies.size()); i++) {
                        Movie movie = movies.get(i);
                        
                        // Create 3 showtimes per movie
                        for (int j = 0; j < 3; j++) {
                            Screen screen = screens.get(j % screens.size());
                            LocalDateTime startTime = LocalDateTime.now()
                                .plusDays(j)
                                .withHour(14 + (j * 3))
                                .withMinute(0);
                            
                            Showtime showtime = Showtime.builder()
                                .movie(movie)
                                .screen(screen)
                                .startTime(startTime)
                                .endTime(startTime.plusHours(2))
                                .price(100000.0)
                                .build();
                            showtimeRepository.save(showtime);
                        }
                    }
                    log.info("✅ Created showtimes for {} movies", Math.min(5, movies.size()));
                }
            }
            
            log.info("🎉 Data initialization completed!");
            
        } catch (Exception e) {
            log.error("❌ Error during data initialization: {}", e.getMessage(), e);
        }
    }
}
