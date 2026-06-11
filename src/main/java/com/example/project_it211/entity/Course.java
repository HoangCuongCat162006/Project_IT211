package com.example.project_it211.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name="curses")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String lecturerName;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean active = true;
    private LocalDateTime createdDate =LocalDateTime.now() ;
}
