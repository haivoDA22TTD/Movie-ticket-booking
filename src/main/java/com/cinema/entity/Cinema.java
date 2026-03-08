package com.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    private String city;
    private String district;
    
    private String phone;
    
    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Screen> screens = new ArrayList<>();
}
