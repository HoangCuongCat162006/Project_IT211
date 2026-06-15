package com.example.project_it211.controller;

import com.example.project_it211.dto.*;
import com.example.project_it211.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(com.example.project_it211.config.SecurityConfig.class)
class StudentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private EnrollmentService enrollmentService;
    @MockBean  private SubmissionService submissionService;
    @MockBean  private UserService userService;
    @MockBean  private LectureMaterialService lectureMaterialService;
    @MockBean  private com.example.project_it211.config.JwtUtil jwtUtil;
    @MockBean  private com.example.project_it211.config.JwtAuthenticationFilter jwtFilter;
    @MockBean  private com.example.project_it211.repository.UserRepository userRepository;

    @Test
    @WithMockUser(username = "sinhvien01", roles = "STUDENT")
    void enroll_success_returns201() throws Exception {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(1L);
        dto.setCourseId(1L);
        dto.setStudentUsername("sinhvien01");

        when(enrollmentService.enrollCourse(eq("sinhvien01"), eq(1L))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/student/enroll")
                        .with(csrf())
                        .param("courseId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentUsername").value("sinhvien01"));
    }

    @Test
    @WithMockUser(username = "sinhvien01", roles = "STUDENT")
    void submitLink_success_returns201() throws Exception {
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(1L);
        dto.setTitle("Đồ án IT211");
        dto.setStatus("SUBMITTED");

        when(submissionService.submitLink(eq("sinhvien01"), any())).thenReturn(dto);

        SubmissionRequest req = new SubmissionRequest();
        req.setCourseId(1L);
        req.setTitle("Đồ án IT211");
        req.setGithubUrl("https://github.com/test/repo");

        mockMvc.perform(post("/api/v1/student/submissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("SUBMITTED"));
    }

    @Test
    @WithMockUser(username = "sinhvien01", roles = "STUDENT")
    void getMySubmissions_returns200() throws Exception {
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(1L);
        dto.setTitle("Đồ án IT211");

        when(submissionService.getMySubmissions("sinhvien01")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/student/submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Đồ án IT211"));
    }

    @Test
    void enroll_withoutAuth_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/student/enroll")
                        .with(csrf())
                        .param("courseId", "1"))
                .andExpect(status().isForbidden());
    }
}
