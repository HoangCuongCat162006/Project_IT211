package com.example.project_it211.service;

import com.example.project_it211.dto.SubmissionDTO;
import com.example.project_it211.dto.SubmissionRequest;
import com.example.project_it211.entity.Course;
import com.example.project_it211.entity.Submission;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.CourseRepository;
import com.example.project_it211.repository.EnrollmentRepository;
import com.example.project_it211.repository.SubmissionRepository;
import com.example.project_it211.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public SubmissionDTO submitLink(String username, SubmissionRequest request) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên: " + username));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + request.getCourseId()));

        // Kiểm tra sinh viên có đăng ký khóa học không
        enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new IllegalArgumentException("Sinh viên chưa đăng ký khóa học này"));

        // Tìm bài nộp cũ (nếu có)
        Submission submission = submissionRepository.findByStudentId(student.getId()).stream()
                .filter(s -> s.getCourse().getId().equals(course.getId()))
                .findFirst()
                .orElse(new Submission());

        submission.setStudent(student);
        submission.setCourse(course);
        submission.setTitle(request.getTitle());
        submission.setDescription(request.getDescription());
        submission.setGithubUrl(request.getGithubUrl());

        LocalDateTime now = LocalDateTime.now();
        submission.setSubmittedAt(now);

        // Kiểm tra nộp muộn
        if (course.getEndDate() != null && now.isAfter(course.getEndDate())) {
            submission.setStatus("LATE");
        } else {
            submission.setStatus("SUBMITTED");
        }

        Submission saved = submissionRepository.save(submission);
        return convertToDTO(saved);
    }

    @Transactional
    public SubmissionDTO uploadReport(String username, Long submissionId, MultipartFile file) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài nộp với ID: " + submissionId));

        if (!submission.getStudent().getUsername().equals(username)) {
            throw new IllegalArgumentException("Bạn không có quyền upload báo cáo cho bài nộp này");
        }

        String reportUrl = cloudinaryService.uploadFile(file);
        submission.setReportUrl(reportUrl);

        LocalDateTime now = LocalDateTime.now();
        submission.setSubmittedAt(now);

        // Nếu bài đã chấm, không đổi trạng thái về SUBMITTED/LATE. Nếu chưa, cập nhật trạng thái nộp bài
        if (!"GRADED".equals(submission.getStatus())) {
            Course course = submission.getCourse();
            if (course.getEndDate() != null && now.isAfter(course.getEndDate())) {
                submission.setStatus("LATE");
            } else {
                submission.setStatus("SUBMITTED");
            }
        }

        Submission saved = submissionRepository.save(submission);
        return convertToDTO(saved);
    }

    public List<SubmissionDTO> getMySubmissions(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên: " + username));

        return submissionRepository.findByStudentId(student.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Cho giảng viên: Xem danh sách bài nộp của khóa học
    public Page<SubmissionDTO> getSubmissionsByCourse(Long courseId, Pageable pageable) {
        return submissionRepository.findByCourseId(courseId, pageable)
                .map(this::convertToDTO);
    }

    // Cho giảng viên: Lọc bài nộp theo trạng thái
    public Page<SubmissionDTO> getSubmissionsByCourseAndStatus(Long courseId, String status, Pageable pageable) {
        return submissionRepository.findByCourseIdAndStatus(courseId, status, pageable)
                .map(this::convertToDTO);
    }

    public SubmissionDTO convertToDTO(Submission s) {
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(s.getId());
        dto.setStudentId(s.getStudent().getId());
        dto.setStudentUsername(s.getStudent().getUsername());
        dto.setCourseId(s.getCourse().getId());
        dto.setCourseName(s.getCourse().getName());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setGithubUrl(s.getGithubUrl());
        dto.setReportUrl(s.getReportUrl());
        dto.setStatus(s.getStatus());
        dto.setSubmittedAt(s.getSubmittedAt());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
