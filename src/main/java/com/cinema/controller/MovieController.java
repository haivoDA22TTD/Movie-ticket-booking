package com.cinema.controller;

import com.cinema.entity.Movie;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.service.MovieService;
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
    
    private final MovieService movieService;
    private final ShowtimeRepository showtimeRepository;
    
    @GetMapping("/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        var showtimes = showtimeRepository.findUpcomingShowtimesByMovie(id, LocalDateTime.now());
        
        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        return "movie-detail";
    }
}
