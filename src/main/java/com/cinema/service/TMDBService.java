package com.cinema.service;

import com.cinema.dto.TMDBMovieResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TMDBService {
    
    @Value("${tmdb.api.key}")
    private String apiKey;
    
    @Value("${tmdb.api.base-url}")
    private String baseUrl;
    
    private final WebClient webClient = WebClient.builder().build();
    
    @Cacheable(value = "nowPlayingMovies", unless = "#result == null")
    public List<TMDBMovieResponse> getNowPlayingMovies() {
        try {
            var response = webClient.get()
                .uri(baseUrl + "/movie/now_playing?language=vi-VN")
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(TMDBApiResponse.class)
                .block();
            
            return response != null ? response.getResults() : List.of();
        } catch (Exception e) {
            log.error("Error fetching now playing movies", e);
            return List.of();
        }
    }
    
    @Cacheable(value = "upcomingMovies", unless = "#result == null")
    public List<TMDBMovieResponse> getUpcomingMovies() {
        try {
            var response = webClient.get()
                .uri(baseUrl + "/movie/upcoming?language=vi-VN")
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(TMDBApiResponse.class)
                .block();
            
            return response != null ? response.getResults() : List.of();
        } catch (Exception e) {
            log.error("Error fetching upcoming movies", e);
            return List.of();
        }
    }
    
    @Cacheable(value = "movieDetails", key = "#movieId")
    public TMDBMovieResponse getMovieDetails(Long movieId) {
        try {
            return webClient.get()
                .uri(baseUrl + "/movie/" + movieId + "?language=vi-VN")
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(TMDBMovieResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Error fetching movie details for id: " + movieId, e);
            return null;
        }
    }
    
    @lombok.Data
    private static class TMDBApiResponse {
        private List<TMDBMovieResponse> results;
    }
}
