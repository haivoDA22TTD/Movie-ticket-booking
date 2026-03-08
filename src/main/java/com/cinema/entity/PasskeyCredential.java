package com.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "passkey_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasskeyCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, unique = true)
    private String credentialId;
    
    @Column(nullable = false, length = 2000)
    private String publicKey;
    
    @Column(nullable = false)
    private Long signatureCount;
    
    @Column(nullable = false)
    private String aaguid;
    
    private String deviceName;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime lastUsedAt;
    
    private boolean enabled = true;
}
