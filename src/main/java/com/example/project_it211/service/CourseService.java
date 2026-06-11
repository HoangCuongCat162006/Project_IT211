package com.example.project_it211.service;

import com.example.project_it211.dto.CourseDTO;
import com.example.project_it211.entity.Course;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // tạo khóa học mới cho admin (FR-05)
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setLecturerName(courseDTO.getLecturerName());
        course.setStartDate(courseDTO.getStartDate());
        course.setEndDate(courseDTO.getEndDate());
        course.setActive(true);

        Course saved = courseRepository.save(course);
        return convertToDTO(saved);
    }

    // lấy tất cả khóa học có phân trang, tìm kiếm và lọc active (FR-05)
    public Page<CourseDTO> getCourses(String search, Boolean active, Pageable pageable) {
        return courseRepository.searchCourses(search, active, pageable)
                .map(this::convertToDTO);
    }

    // lấy tất cả khóa học (cũ)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // cập nhật thông tin khóa học (FR-05)
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));

        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setLecturerName(courseDTO.getLecturerName());
        course.setStartDate(courseDTO.getStartDate());
        course.setEndDate(courseDTO.getEndDate());
        course.setActive(courseDTO.isActive());

        Course updated = courseRepository.save(course);
        return convertToDTO(updated);
    }

    // vô hiệu hóa khóa học (FR-05)
    public void deactivateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        course.setActive(false);
        courseRepository.save(course);
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setLecturerName(course.getLecturerName());
        dto.setStartDate(course.getStartDate());
        dto.setEndDate(course.getEndDate());
        dto.setActive(course.isActive());
        return dto;
    }
}
