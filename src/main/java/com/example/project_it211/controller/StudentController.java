package com.example.project_it211.controller;

import com.example.project_it211.dto.ApiResponse;
import com.example.project_it211.dto.ChangePasswordRequest;
import com.example.project_it211.dto.EnrollmentDTO;
import com.example.project_it211.dto.LectureMaterialDTO;
import com.example.project_it211.dto.SubmissionDTO;
import com.example.project_it211.dto.SubmissionRequest;
import com.example.project_it211.service.EnrollmentService;
import com.example.project_it211.service.LectureMaterialService;
import com.example.project_it211.service.SubmissionService;
import com.example.project_it211.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private LectureMaterialService lectureMaterialService;

    // sinh viên đăng ký khóa học (FR-06)
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<EnrollmentDTO>> enrollCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        EnrollmentDTO dto = enrollmentService.enrollCourse(studentId, courseId);
        return new ResponseEntity<>(
                ApiResponse.success("Đăng ký khóa học thành công", dto),
                HttpStatus.CREATED
        );
    }

    // Sinh viên nộp link GitHub (FR-07)
    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<SubmissionDTO>> submitLink(
            Principal principal,
            @Valid @RequestBody SubmissionRequest request) {
        SubmissionDTO dto = submissionService.submitLink(principal.getName(), request);
        return new ResponseEntity<>(
                ApiResponse.success("Nộp link bài tập thành công", dto),
                HttpStatus.CREATED
        );
    }

    // Sinh viên upload file báo cáo lên Cloudinary (UC-05)
    @PostMapping("/submissions/{id}/upload")
    public ResponseEntity<ApiResponse<SubmissionDTO>> uploadReport(
            Principal principal,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        SubmissionDTO dto = submissionService.uploadReport(principal.getName(), id, file);
        return ResponseEntity.ok(ApiResponse.success("Upload báo cáo thành công", dto));
    }

    // Sinh viên xem danh sách bài nộp của mình (FR-08)
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<List<SubmissionDTO>>> getMySubmissions(Principal principal) {
        List<SubmissionDTO> list = submissionService.getMySubmissions(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài nộp thành công", list));
    }

    // Sinh viên đổi mật khẩu (FR-10)
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    // Sinh viên xem tài liệu khóa học (FR-09)
    @GetMapping("/materials/{courseId}")
    public ResponseEntity<ApiResponse<Page<LectureMaterialDTO>>> getMaterials(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LectureMaterialDTO> list = lectureMaterialService.getMaterialsByCourse(courseId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tài liệu học tập thành công", list));
    }
}
