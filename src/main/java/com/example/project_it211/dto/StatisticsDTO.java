package com.example.project_it211.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private long totalUsers;
    private long totalStudents;
    private long totalLecturers;
    private long totalAdmins;
    private long totalCourses;
    private long activeCourses;
    private long totalSubmissions;
    private long gradedSubmissions;
    private long pendingSubmissions;
    private Double averageScore;
}
