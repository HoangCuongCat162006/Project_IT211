package com.example.project_it211.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LectureMaterialDTO {
    private Long id;
    private Long courseId;
    private String courseName;
    private Long lecturerId;
    private String lecturerUsername;
    private String title;
    private String fileUrl;
    private String fileType;
    private LocalDateTime uploadedAt;
}
