package com.example.project_it211.service;

import com.example.project_it211.config.JwtUtil;
import com.example.project_it211.dto.AuthResponse;
import com.example.project_it211.dto.LoginRequest;
import com.example.project_it211.entity.Role;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.TokenBlacklistRepository;
import com.example.project_it211.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private TokenBlacklistRepository tokenBlacklistRepository;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("sinhvien01");
        mockUser.setRole(Role.STUDENT);
        mockUser.setActive(true);
    }

    @Test
    void login_success_returnsTokens() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh-token");

        LoginRequest req = new LoginRequest();
        req.setUsername("sinhvien01");
        req.setPassword("123456");

        AuthResponse res = authService.login(req);

        assertEquals("access-token", res.getAccessToken());
        assertEquals("STUDENT", res.getRole());
    }

    @Test
    void login_userNotFound_throwsException() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setUsername("unknown");
        req.setPassword("pass");

        assertThrows(ResourceNotFoundException.class, () -> authService.login(req));
    }

    @Test
    void login_inactiveAccount_throwsException() {
        mockUser.setActive(false);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.of(mockUser));

        LoginRequest req = new LoginRequest();
        req.setUsername("sinhvien01");
        req.setPassword("123456");

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void logout_validToken_savesToBlacklist() {
        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(tokenBlacklistRepository.existsByToken("tok")).thenReturn(false);
        when(jwtUtil.extractExpiration("tok"))
                .thenReturn(new Date(System.currentTimeMillis() + 60000));
        when(tokenBlacklistRepository.save(any())).thenReturn(null);

        assertDoesNotThrow(() -> authService.logout("Bearer tok"));
        verify(tokenBlacklistRepository).save(any());
    }

    @Test
    void logout_invalidHeader_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> authService.logout("NoBearer"));
    }
}
