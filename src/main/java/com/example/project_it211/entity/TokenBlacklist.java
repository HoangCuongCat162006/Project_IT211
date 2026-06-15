package com.example.project_it211.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Data
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String token;

    private LocalDateTime expiresAt;

    private LocalDateTime revokedAt = LocalDateTime.now();
}
