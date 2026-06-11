package com.example.project_it211.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GradeDTO {
    private Long id;
    private Long submissionId;
    private String submissionTitle;
    private Long lecturerId;
    private String lecturerUsername;
    private Double score;
    private String feedback;
    private LocalDateTime gradedAt;
}
