package com.cinema.controller;

import com.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final MovieService movieService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("nowShowingMovies", movieService.getNowShowingMovies());
        model.addAttribute("comingSoonMovies", movieService.getComingSoonMovies());
        return "index";
    }
    
    @GetMapping("/admin/sync-movies")
    public String syncMovies() {
        movieService.syncMoviesFromTMDB();
        return "redirect:/";
    }
    
    @GetMapping("/admin/clear-cache")
    @ResponseBody
    public String clearCache() {
        movieService.clearCache();
        return "Cache cleared! <a href='/'>Go to home</a>";
    }
}
