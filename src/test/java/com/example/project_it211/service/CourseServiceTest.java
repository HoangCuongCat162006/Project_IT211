package com.example.project_it211.service;

import com.example.project_it211.dto.CourseDTO;
import com.example.project_it211.entity.Course;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.CourseRepository;
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
class CourseServiceTest {

    @Mock private CourseRepository courseRepository;
    @InjectMocks private CourseService courseService;

    private Course mockCourse;

    @BeforeEach
    void setUp() {
        mockCourse = new Course();
        mockCourse.setId(1L);
        mockCourse.setName("Java Web Service");
        mockCourse.setDescription("Spring Boot RESTful API");
        mockCourse.setLecturerName("giangvien01");
        mockCourse.setActive(true);
    }

    @Test
    void createCourse_success() {
        when(courseRepository.save(any(Course.class))).thenReturn(mockCourse);

        CourseDTO dto = new CourseDTO();
        dto.setName("Java Web Service");
        dto.setLecturerName("giangvien01");
        dto.setActive(true);

        CourseDTO result = courseService.createCourse(dto);

        assertNotNull(result);
        assertEquals("Java Web Service", result.getName());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse_success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        mockCourse.setName("Java Web Service Nâng Cao");
        when(courseRepository.save(any(Course.class))).thenReturn(mockCourse);

        CourseDTO dto = new CourseDTO();
        dto.setName("Java Web Service Nâng Cao");
        dto.setLecturerName("giangvien01");
        dto.setActive(true);

        CourseDTO result = courseService.updateCourse(1L, dto);
        assertEquals("Java Web Service Nâng Cao", result.getName());
    }

    @Test
    void deactivateCourse_success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(any())).thenReturn(mockCourse);

        courseService.deactivateCourse(1L);

        assertFalse(mockCourse.isActive());
    }

    @Test
    void updateCourse_notFound_throwsException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        CourseDTO dto = new CourseDTO();
        dto.setName("X");

        assertThrows(ResourceNotFoundException.class,
                () -> courseService.updateCourse(99L, dto));
    }
}
