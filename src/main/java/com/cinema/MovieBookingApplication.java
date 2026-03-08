package com.cinema;

import com.cinema.entity.User;
import com.cinema.repository.UserRepository;
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
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
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
            } else {
                log.info("Admin account already exists");
            }
        };
    }
}
