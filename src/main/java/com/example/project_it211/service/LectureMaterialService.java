package com.example.project_it211.service;

import com.example.project_it211.dto.LectureMaterialDTO;
import com.example.project_it211.entity.Course;
import com.example.project_it211.entity.LectureMaterial;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.CourseRepository;
import com.example.project_it211.repository.LectureMaterialRepository;
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
public class LectureMaterialService {

    @Autowired
    private LectureMaterialRepository lectureMaterialRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public LectureMaterialDTO uploadMaterial(String lecturerUsername, Long courseId, String title, MultipartFile file) {
        User lecturer = userRepository.findByUsername(lecturerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên: " + lecturerUsername));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        String fileUrl = cloudinaryService.uploadFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileType = "unknown";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        } else if (file.getContentType() != null) {
            fileType = file.getContentType();
        }

        LectureMaterial material = new LectureMaterial();
        material.setCourse(course);
        material.setLecturer(lecturer);
        material.setTitle(title);
        material.setFileUrl(fileUrl);
        material.setFileType(fileType);
        material.setUploadedAt(LocalDateTime.now());

        LectureMaterial saved = lectureMaterialRepository.save(material);
        return convertToDTO(saved);
    }

    public List<LectureMaterialDTO> getMaterialsByCourse(Long courseId) {
        return lectureMaterialRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<LectureMaterialDTO> getMaterialsByCourse(Long courseId, Pageable pageable) {
        return lectureMaterialRepository.findByCourseId(courseId, pageable)
                .map(this::convertToDTO);
    }

    private LectureMaterialDTO convertToDTO(LectureMaterial m) {
        LectureMaterialDTO dto = new LectureMaterialDTO();
        dto.setId(m.getId());
        dto.setCourseId(m.getCourse().getId());
        dto.setCourseName(m.getCourse().getName());
        dto.setLecturerId(m.getLecturer().getId());
        dto.setLecturerUsername(m.getLecturer().getUsername());
        dto.setTitle(m.getTitle());
        dto.setFileUrl(m.getFileUrl());
        dto.setFileType(m.getFileType());
        dto.setUploadedAt(m.getUploadedAt());
        return dto;
    }
}
