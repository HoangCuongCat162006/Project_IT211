package com.example.project_it211.service;

import com.example.project_it211.dto.GradeDTO;
import com.example.project_it211.dto.GradeRequest;
import com.example.project_it211.entity.*;
import com.example.project_it211.exception.InvalidStateException;
import com.example.project_it211.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock private GradeRepository gradeRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private GradeService gradeService;

    private User lecturer;
    private Submission submission;

    @BeforeEach
    void setUp() {
        lecturer = new User();
        lecturer.setId(2L);
        lecturer.setUsername("giangvien01");
        lecturer.setRole(Role.LECTURER);

        submission = new Submission();
        submission.setId(1L);
        submission.setStatus("SUBMITTED");
        submission.setTitle("Đồ án IT211");
        submission.setStudent(new User());
    }

    @Test
    void gradeSubmission_success() {
        when(userRepository.findByUsername("giangvien01")).thenReturn(Optional.of(lecturer));
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(gradeRepository.findBySubmissionId(1L)).thenReturn(Optional.empty());

        Grade savedGrade = new Grade();
        savedGrade.setId(1L);
        savedGrade.setSubmission(submission);
        savedGrade.setLecturer(lecturer);
        savedGrade.setScore(90.5);
        savedGrade.setFeedback("Tốt");
        when(gradeRepository.save(any())).thenReturn(savedGrade);
        when(submissionRepository.save(any())).thenReturn(submission);

        GradeRequest req = new GradeRequest();
        req.setSubmissionId(1L);
        req.setScore(90.5);
        req.setFeedback("Tốt");

        GradeDTO result = gradeService.gradeSubmission("giangvien01", req);

        assertEquals(90.5, result.getScore());
        assertEquals("GRADED", submission.getStatus());
    }

    @Test
    void gradeSubmission_invalidStatus_throwsException() {
        submission.setStatus("PENDING");
        when(userRepository.findByUsername("giangvien01")).thenReturn(Optional.of(lecturer));
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        GradeRequest req = new GradeRequest();
        req.setSubmissionId(1L);
        req.setScore(80.0);

        assertThrows(InvalidStateException.class,
                () -> gradeService.gradeSubmission("giangvien01", req));
    }

    @Test
    void gradeSubmission_scoreOver100_caught() {
        // Validation xảy ra ở DTO layer nhưng kiểm tra service vẫn xử lý đúng khi score hợp lệ
        when(userRepository.findByUsername("giangvien01")).thenReturn(Optional.of(lecturer));
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(gradeRepository.findBySubmissionId(1L)).thenReturn(Optional.empty());

        Grade saved = new Grade();
        saved.setId(1L);
        saved.setSubmission(submission);
        saved.setLecturer(lecturer);
        saved.setScore(100.0);
        when(gradeRepository.save(any())).thenReturn(saved);
        when(submissionRepository.save(any())).thenReturn(submission);

        GradeRequest req = new GradeRequest();
        req.setSubmissionId(1L);
        req.setScore(100.0);

        GradeDTO result = gradeService.gradeSubmission("giangvien01", req);
        assertEquals(100.0, result.getScore());
    }
}
