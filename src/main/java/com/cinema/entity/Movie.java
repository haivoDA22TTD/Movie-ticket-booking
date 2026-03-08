package com.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private Long tmdbId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String overview;
    
    private String posterPath;
    private String backdropPath;
    
    private LocalDate releaseDate;
    
    private Integer runtime; // minutes
    
    private Double voteAverage;
    
    @ElementCollection
    private List<String> genres = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private MovieStatus status = MovieStatus.NOW_SHOWING;
    
    private String trailerUrl;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Showtime> showtimes = new ArrayList<>();
    
    public enum MovieStatus {
        COMING_SOON, NOW_SHOWING, ENDED
    }
}
