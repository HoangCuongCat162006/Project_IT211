package com.example.project_it211.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequest {
    @NotNull(message = "Submission ID không được để trống")
    private Long submissionId;

    @NotNull(message = "Điểm số không được để trống")
    @DecimalMin(value = "0.0", message = "Điểm phải từ 0 đến 100")
    @DecimalMax(value = "100.0", message = "Điểm phải từ 0 đến 100")
    private Double score;

    private String feedback;
}
