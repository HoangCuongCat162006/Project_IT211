package com.example.project_it211.repository;

import com.example.project_it211.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Lấy bài nộp của một sinh viên
    List<Submission> findByStudentId(Long studentId);

    // Lấy bài nộp theo khóa học (cho giảng viên)
    Page<Submission> findByCourseId(Long courseId, Pageable pageable);

    // Lấy bài nộp theo status
    Page<Submission> findByCourseIdAndStatus(Long courseId, String status, Pageable pageable);

    // Kiểm tra sinh viên đã nộp bài vào khóa học chưa
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    // Đếm bài nộp theo status
    long countByStatus(String status);
}
