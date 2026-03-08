package com.cinema;

import com.cinema.entity.User;
import com.cinema.repository.UserRepository;
import com.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    CommandLineRunner initData(MovieService movieService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin account if not exists
            if (!userRepository.existsByEmail("admin@cinema.vn")) {
                log.info("Creating default admin account...");
                User admin = User.builder()
                    .email("admin@cinema.vn")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .phone("0000000000")
                    .role(User.Role.ADMIN)
                    .enabled(true)
                    .build();
                userRepository.save(admin);
                log.info("Admin account created: admin@cinema.vn / admin123");
            }
            
            // Sync movies from TMDB
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
