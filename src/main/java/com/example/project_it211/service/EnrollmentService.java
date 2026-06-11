package com.example.project_it211.service;

import com.example.project_it211.dto.EnrollmentDTO;
import com.example.project_it211.entity.Course;
import com.example.project_it211.entity.Enrollment;
import com.example.project_it211.entity.Role;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.DuplicateResourceException;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.CourseRepository;
import com.example.project_it211.repository.EnrollmentRepository;
import com.example.project_it211.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    // sinh viên đăng ký khóa học (FR-06)
    public EnrollmentDTO enrollCourse(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Người dùng không phải là sinh viên");
        }

        if (!course.isActive()) {
            throw new IllegalArgumentException("Khóa học không hoạt động");
        }

        if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new DuplicateResourceException("Sinh viên đã đăng ký khóa học này trước đó");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return convertToDTO(saved);
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setStatus(enrollment.getStatus());
        return dto;
    }
}
