package com.example.project_it211.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentUsername;
    private Long courseId;
    private LocalDateTime enrolledAt;
    private String status;
}
