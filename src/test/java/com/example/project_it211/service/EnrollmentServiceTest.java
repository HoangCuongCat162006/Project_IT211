package com.example.project_it211.service;

import com.example.project_it211.dto.EnrollmentDTO;
import com.example.project_it211.entity.*;
import com.example.project_it211.exception.DuplicateResourceException;
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
class EnrollmentServiceTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private CourseRepository courseRepository;
    @InjectMocks private EnrollmentService enrollmentService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("sinhvien01");
        student.setRole(Role.STUDENT);
        student.setActive(true);

        course = new Course();
        course.setId(1L);
        course.setName("Java Web Service");
        course.setActive(true);
    }

    @Test
    void enrollCourse_success() {
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());

        Enrollment saved = new Enrollment();
        saved.setId(1L);
        saved.setStudent(student);
        saved.setCourse(course);
        when(enrollmentRepository.save(any())).thenReturn(saved);

        EnrollmentDTO result = enrollmentService.enrollCourse("sinhvien01", 1L);

        assertNotNull(result);
        assertEquals(1L, result.getCourseId());
        assertEquals("sinhvien01", result.getStudentUsername());
    }

    @Test
    void enrollCourse_alreadyEnrolled_throwsException() {
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L))
                .thenReturn(Optional.of(new Enrollment()));

        assertThrows(DuplicateResourceException.class,
                () -> enrollmentService.enrollCourse("sinhvien01", 1L));
    }

    @Test
    void enrollCourse_inactiveCourse_throwsException() {
        course.setActive(false);
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(IllegalArgumentException.class,
                () -> enrollmentService.enrollCourse("sinhvien01", 1L));
    }
}
