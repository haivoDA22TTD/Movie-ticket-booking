package com.cinema.controller;

import com.cinema.dto.TMDBMovieResponse;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.service.TMDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    
    private final TMDBService tmdbService;
    private final ShowtimeRepository showtimeRepository;
    
    @GetMapping("/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        // Get movie from TMDB API
        TMDBMovieResponse movie = tmdbService.getMovieDetails(id);
        
        // Get showtimes from database
        var showtimes = showtimeRepository.findUpcomingShowtimesByMovie(id, LocalDateTime.now());
        
        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        return "movie-detail";
    }
}
