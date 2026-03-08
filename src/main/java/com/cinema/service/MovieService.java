package com.cinema.service;

import com.cinema.dto.TMDBMovieResponse;
import com.cinema.entity.Movie;
import com.cinema.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    
    private final MovieRepository movieRepository;
    private final TMDBService tmdbService;
    
    @Cacheable(value = "movies", key = "'nowShowing'")
    public List<Movie> getNowShowingMovies() {
        List<Movie> movies = movieRepository.findByStatus(Movie.MovieStatus.NOW_SHOWING);
        log.info("Found {} now showing movies in database", movies.size());
        return movies;
    }
    
    @Cacheable(value = "movies", key = "'comingSoon'")
    public List<Movie> getComingSoonMovies() {
        List<Movie> movies = movieRepository.findByStatus(Movie.MovieStatus.COMING_SOON);
        log.info("Found {} coming soon movies in database", movies.size());
        return movies;
    }
    
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found"));
    }
    
    @Transactional
    public void syncMoviesFromTMDB() {
        log.info("Starting TMDB sync...");
        List<TMDBMovieResponse> nowPlaying = tmdbService.getNowPlayingMovies();
        List<TMDBMovieResponse> upcoming = tmdbService.getUpcomingMovies();
        
        log.info("Received {} now playing and {} upcoming movies from TMDB", 
            nowPlaying.size(), upcoming.size());
        
        nowPlaying.forEach(tmdbMovie -> saveOrUpdateMovie(tmdbMovie, Movie.MovieStatus.NOW_SHOWING));
        upcoming.forEach(tmdbMovie -> saveOrUpdateMovie(tmdbMovie, Movie.MovieStatus.COMING_SOON));
        
        log.info("TMDB sync completed!");
    }
    
    private void saveOrUpdateMovie(TMDBMovieResponse tmdbMovie, Movie.MovieStatus status) {
        Movie movie = movieRepository.findByTmdbId(tmdbMovie.getId())
            .orElse(new Movie());
        
        movie.setTmdbId(tmdbMovie.getId());
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOverview(tmdbMovie.getOverview());
        movie.setPosterPath(tmdbMovie.getPosterPath());
        movie.setBackdropPath(tmdbMovie.getBackdropPath());
        movie.setReleaseDate(tmdbMovie.getReleaseDate() != null ? 
            LocalDate.parse(tmdbMovie.getReleaseDate()) : null);
        movie.setRuntime(tmdbMovie.getRuntime());
        movie.setVoteAverage(tmdbMovie.getVoteAverage());
        movie.setStatus(status);
        
        if (tmdbMovie.getGenres() != null) {
            movie.setGenres(tmdbMovie.getGenres().stream()
                .map(TMDBMovieResponse.Genre::getName)
                .collect(Collectors.toList()));
        }
        
        movieRepository.save(movie);
    }
}
