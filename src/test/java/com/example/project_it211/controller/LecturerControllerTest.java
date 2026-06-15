package com.example.project_it211.controller;

import com.example.project_it211.dto.*;
import com.example.project_it211.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LecturerController.class)
@Import(com.example.project_it211.config.SecurityConfig.class)
class LecturerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private GradeService gradeService;
    @MockBean  private SubmissionService submissionService;
    @MockBean  private LectureMaterialService lectureMaterialService;
    @MockBean  private com.example.project_it211.config.JwtUtil jwtUtil;
    @MockBean  private com.example.project_it211.config.JwtAuthenticationFilter jwtFilter;
    @MockBean  private com.example.project_it211.repository.UserRepository userRepository;

    @Test
    @WithMockUser(username = "giangvien01", roles = "LECTURER")
    void gradeSubmission_success_returns200() throws Exception {
        GradeDTO grade = new GradeDTO();
        grade.setId(1L);
        grade.setSubmissionId(1L);
        grade.setScore(90.5);
        grade.setFeedback("Tốt");

        when(gradeService.gradeSubmission(eq("giangvien01"), any())).thenReturn(grade);

        GradeRequest req = new GradeRequest();
        req.setSubmissionId(1L);
        req.setScore(90.5);
        req.setFeedback("Tốt");

        mockMvc.perform(post("/api/v1/lecturer/grades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(90.5))
                .andExpect(jsonPath("$.data.feedback").value("Tốt"));
    }

    @Test
    @WithMockUser(username = "giangvien01", roles = "LECTURER")
    void getSubmissions_returns200() throws Exception {
        SubmissionDTO sub = new SubmissionDTO();
        sub.setId(1L);
        sub.setStatus("SUBMITTED");

        when(submissionService.getSubmissionsByCourse(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sub)));

        mockMvc.perform(get("/api/v1/lecturer/submissions")
                        .param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].status").value("SUBMITTED"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void gradeSubmission_asStudent_returns403() throws Exception {
        GradeRequest req = new GradeRequest();
        req.setSubmissionId(1L);
        req.setScore(90.0);

        mockMvc.perform(post("/api/v1/lecturer/grades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}
