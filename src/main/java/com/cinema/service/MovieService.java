package com.cinema.service;

import com.cinema.dto.TMDBMovieResponse;
import com.cinema.entity.Movie;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    
    private final MovieRepository movieRepository;
    private final TMDBService tmdbService;
    private final ShowtimeRepository showtimeRepository;
    
    // Lấy phim CÓ LỊCH CHIẾU từ database
    public List<TMDBMovieResponse> getMoviesWithShowtimes() {
        // Lấy danh sách movie IDs có lịch chiếu
        List<Long> movieIdsWithShowtimes = showtimeRepository
            .findUpcomingShowtimes(LocalDateTime.now())
            .stream()
            .map(showtime -> showtime.getMovie().getId())
            .distinct()
            .toList();
        
        // Lấy thông tin chi tiết từ TMDB cho các phim có lịch chiếu
        return movieIdsWithShowtimes.stream()
            .map(tmdbService::getMovieDetails)
            .filter(movie -> movie != null)
            .toList();
    }
    
    public List<TMDBMovieResponse> getComingSoonMovies() {
        return tmdbService.getUpcomingMovies();
    }
    
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found"));
    }
    
    @Transactional
    public void syncMoviesFromTMDB() {
        List<TMDBMovieResponse> nowPlaying = tmdbService.getNowPlayingMovies();
        List<TMDBMovieResponse> upcoming = tmdbService.getUpcomingMovies();
        
        nowPlaying.forEach(tmdbMovie -> saveOrUpdateMovie(tmdbMovie, Movie.MovieStatus.NOW_SHOWING));
        upcoming.forEach(tmdbMovie -> saveOrUpdateMovie(tmdbMovie, Movie.MovieStatus.COMING_SOON));
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
