package com.cinema.controller;

import com.cinema.entity.Cinema;
import com.cinema.entity.Screen;
import com.cinema.entity.Showtime;
import com.cinema.repository.CinemaRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.ScreenRepository;
import com.cinema.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final CinemaRepository cinemaRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("cinemaCount", cinemaRepository.count());
        model.addAttribute("screenCount", screenRepository.count());
        model.addAttribute("showtimeCount", showtimeRepository.count());
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
