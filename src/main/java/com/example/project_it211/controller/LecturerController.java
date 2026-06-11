package com.example.project_it211.controller;

import com.example.project_it211.dto.ApiResponse;
import com.example.project_it211.dto.GradeDTO;
import com.example.project_it211.dto.GradeRequest;
import com.example.project_it211.dto.LectureMaterialDTO;
import com.example.project_it211.dto.SubmissionDTO;
import com.example.project_it211.service.GradeService;
import com.example.project_it211.service.LectureMaterialService;
import com.example.project_it211.service.SubmissionService;
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

@RestController
@RequestMapping("/api/v1/lecturer")
public class LecturerController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private LectureMaterialService lectureMaterialService;

    // Giảng viên chấm điểm đồ án (FR-08)
    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<GradeDTO>> gradeSubmission(
            Principal principal,
            @Valid @RequestBody GradeRequest request) {
        GradeDTO dto = gradeService.gradeSubmission(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Chấm điểm bài nộp thành công", dto));
    }

    // Giảng viên xem & lọc danh sách bài nộp cần chấm (FR-08)
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<Page<SubmissionDTO>>> getSubmissions(
            @RequestParam Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionDTO> result;
        if (status == null || status.trim().isEmpty()) {
            result = submissionService.getSubmissionsByCourse(courseId, pageable);
        } else {
            result = submissionService.getSubmissionsByCourseAndStatus(courseId, status, pageable);
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài nộp thành công", result));
    }

    // Giảng viên tải lên tài liệu học tập (FR-09)
    @PostMapping("/materials")
    public ResponseEntity<ApiResponse<LectureMaterialDTO>> uploadMaterial(
            Principal principal,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam("file") MultipartFile file) {
        LectureMaterialDTO dto = lectureMaterialService.uploadMaterial(principal.getName(), courseId, title, file);
        return new ResponseEntity<>(
                ApiResponse.success("Upload tài liệu khóa học thành công", dto),
                HttpStatus.CREATED
        );
    }

    // Học viên/Giảng viên xem danh sách tài liệu học tập (FR-09)
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
