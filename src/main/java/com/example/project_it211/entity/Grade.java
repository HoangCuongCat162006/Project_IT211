package com.example.project_it211.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;

    // Điểm từ 0 đến 100
    @Column(nullable = false)
    private Double score;

    @Column(length = 2000)
    private String feedback;

    private LocalDateTime gradedAt = LocalDateTime.now();
}
