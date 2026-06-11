package com.example.project_it211.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDTO {
    private Long id;
    private String name;
    private String description;
    private String lecturerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

}
