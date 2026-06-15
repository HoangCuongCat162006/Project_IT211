package com.example.project_it211.controller;

import com.example.project_it211.dto.*;
import com.example.project_it211.entity.Role;
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

@WebMvcTest(AdminController.class)
@Import(com.example.project_it211.config.SecurityConfig.class)
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private UserService userService;
    @MockBean  private CourseService courseService;
    @MockBean  private com.example.project_it211.config.JwtUtil jwtUtil;
    @MockBean  private com.example.project_it211.config.JwtAuthenticationFilter jwtFilter;
    @MockBean  private com.example.project_it211.repository.UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_returns200WithPage() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("sinhvien01");
        userDTO.setRole(Role.STUDENT);

        when(userService.getUsers(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(userDTO)));

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("sinhvien01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_returns201() throws Exception {
        UserDTO created = new UserDTO();
        created.setId(2L);
        created.setUsername("giangvien01");
        created.setRole(Role.LECTURER);

        when(userService.createUser(any())).thenReturn(created);

        UserDTO req = new UserDTO();
        req.setUsername("giangvien01");
        req.setEmail("gv01@example.com");
        req.setRole(Role.LECTURER);

        mockMvc.perform(post("/api/v1/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("LECTURER"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getUsers_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_returns201() throws Exception {
        CourseDTO created = new CourseDTO();
        created.setId(1L);
        created.setName("Java Web Service");
        created.setActive(true);

        when(courseService.createCourse(any())).thenReturn(created);

        CourseDTO req = new CourseDTO();
        req.setName("Java Web Service");
        req.setLecturerName("giangvien01");
        req.setActive(true);

        mockMvc.perform(post("/api/v1/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Java Web Service"));
    }
}
