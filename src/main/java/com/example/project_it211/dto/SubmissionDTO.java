package com.example.project_it211.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubmissionDTO {
    private Long id;
    private Long studentId;
    private String studentUsername;
    private Long courseId;
    private String courseName;
    private String title;
    private String description;
    private String githubUrl;
    private String reportUrl;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
}
