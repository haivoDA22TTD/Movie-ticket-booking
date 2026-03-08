package com.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String seatRow;
    
    @Column(nullable = false)
    private Integer seatNumber;
    
    @Enumerated(EnumType.STRING)
    private SeatType type = SeatType.STANDARD;
    
    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;
    
    public enum SeatType {
        STANDARD, VIP, COUPLE
    }
}
