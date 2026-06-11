package com.example.project_it211.dto;

import lombok.Data;

@Data
public class SubmissionRequest {
    private Long courseId;
    private String title;
    private String description;
    private String githubUrl;
}
