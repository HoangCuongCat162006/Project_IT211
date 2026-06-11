package com.example.project_it211.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_materials")
@Data
public class LectureMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;

    @Column(nullable = false)
    private String title;

    // URL file lưu trên Cloudinary/S3
    @Column(nullable = false)
    private String fileUrl;

    private String fileType;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
