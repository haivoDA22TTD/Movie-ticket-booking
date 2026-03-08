package com.cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TMDBMovieResponse {
    private Long id;
    private String title;
    private String overview;
    
    @JsonProperty("poster_path")
    private String posterPath;
    
    @JsonProperty("backdrop_path")
    private String backdropPath;
    
    @JsonProperty("release_date")
    private String releaseDate;
    
    private Integer runtime;
    
    @JsonProperty("vote_average")
    private Double voteAverage;
    
    private List<Genre> genres;
    
    @Data
    public static class Genre {
        private Long id;
        private String name;
    }
}
