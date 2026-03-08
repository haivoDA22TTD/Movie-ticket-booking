package com.cinema;

import com.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class MovieBookingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MovieBookingApplication.class, args);
    }
    
    @Bean
    CommandLineRunner initData(MovieService movieService) {
        return args -> {
            log.info("Syncing movies from TMDB...");
            try {
                movieService.syncMoviesFromTMDB();
                log.info("Movies synced successfully!");
            } catch (Exception e) {
                log.error("Failed to sync movies: " + e.getMessage());
            }
        };
    }
}
