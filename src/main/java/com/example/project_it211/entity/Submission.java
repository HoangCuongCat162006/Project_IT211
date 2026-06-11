package com.example.project_it211.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String githubUrl;

    private String reportUrl;

    @Column(nullable = false)
    private String status = "PENDING";

    private String title;
    private String description;

    private LocalDateTime submittedAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
