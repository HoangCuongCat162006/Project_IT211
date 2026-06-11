package com.example.project_it211.service;

import com.example.project_it211.dto.GradeDTO;
import com.example.project_it211.dto.GradeRequest;
import com.example.project_it211.entity.Grade;
import com.example.project_it211.entity.Submission;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.InvalidStateException;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.GradeRepository;
import com.example.project_it211.repository.SubmissionRepository;
import com.example.project_it211.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public GradeDTO gradeSubmission(String lecturerUsername, GradeRequest request) {
        User lecturer = userRepository.findByUsername(lecturerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên: " + lecturerUsername));

        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài nộp với ID: " + request.getSubmissionId()));

        // Kiểm tra trạng thái bài nộp phải là SUBMITTED hoặc LATE (hoặc GRADED nếu muốn chấm lại)
        if (!"SUBMITTED".equals(submission.getStatus()) &&
                !"LATE".equals(submission.getStatus()) &&
                !"GRADED".equals(submission.getStatus())) {
            throw new InvalidStateException("Bài nộp chưa được nộp hoặc không hợp lệ để chấm điểm");
        }

        Grade grade = gradeRepository.findBySubmissionId(submission.getId())
                .orElse(new Grade());

        grade.setSubmission(submission);
        grade.setLecturer(lecturer);
        grade.setScore(request.getScore());
        grade.setFeedback(request.getFeedback());
        grade.setGradedAt(LocalDateTime.now());

        Grade savedGrade = gradeRepository.save(grade);

        submission.setStatus("GRADED");
        submissionRepository.save(submission);

        return convertToDTO(savedGrade);
    }

    public GradeDTO getGradeBySubmission(Long submissionId) {
        Grade grade = gradeRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa chấm điểm cho bài nộp này"));
        return convertToDTO(grade);
    }

    private GradeDTO convertToDTO(Grade g) {
        GradeDTO dto = new GradeDTO();
        dto.setId(g.getId());
        dto.setSubmissionId(g.getSubmission().getId());
        dto.setSubmissionTitle(g.getSubmission().getTitle());
        dto.setLecturerId(g.getLecturer().getId());
        dto.setLecturerUsername(g.getLecturer().getUsername());
        dto.setScore(g.getScore());
        dto.setFeedback(g.getFeedback());
        dto.setGradedAt(g.getGradedAt());
        return dto;
    }
}
