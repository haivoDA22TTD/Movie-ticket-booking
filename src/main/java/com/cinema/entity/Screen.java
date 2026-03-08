package com.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private Integer totalSeats;
    
    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
    
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();
    
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Showtime> showtimes = new ArrayList<>();
}
