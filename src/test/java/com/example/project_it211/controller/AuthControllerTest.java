package com.example.project_it211.controller;

import com.example.project_it211.dto.*;
import com.example.project_it211.entity.Role;
import com.example.project_it211.service.AuthService;
import com.example.project_it211.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(com.example.project_it211.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private AuthService authService;
    @MockBean  private UserService userService;
    @MockBean  private com.example.project_it211.config.JwtUtil jwtUtil;
    @MockBean  private com.example.project_it211.config.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean  private com.example.project_it211.repository.UserRepository userRepository;

    @Test
    void register_success_returns201() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("sinhvien01");
        userDTO.setEmail("sv01@example.com");
        userDTO.setRole(Role.STUDENT);

        when(userService.registerStudent(any())).thenReturn(userDTO);

        RegisterRequest req = new RegisterRequest();
        req.setUsername("sinhvien01");
        req.setEmail("sv01@example.com");
        req.setPassword("123456");
        req.setFullName("Sinh Vien 01");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("sinhvien01"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    void login_success_returns200() throws Exception {
        AuthResponse authResp = AuthResponse.builder()
                .accessToken("access-token").refreshToken("refresh-token")
                .tokenType("Bearer").username("admin").role("ADMIN")
                .build();
        when(authService.login(any())).thenReturn(authResp);

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void forgotPassword_returns200() throws Exception {
        when(userService.forgotPassword(any())).thenReturn("Reset123456");

        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setUsername("sinhvien01");
        req.setEmail("sv01@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newPassword").value("Reset123456"));
    }

    @Test
    @WithMockUser
    void logout_withToken_returns200() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer some-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
