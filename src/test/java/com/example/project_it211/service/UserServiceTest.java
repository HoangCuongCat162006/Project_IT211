package com.example.project_it211.service;

import com.example.project_it211.dto.RegisterRequest;
import com.example.project_it211.dto.UserDTO;
import com.example.project_it211.entity.Role;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.DuplicateResourceException;
import com.example.project_it211.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("sinhvien01");
        mockUser.setEmail("sinhvien01@example.com");
        mockUser.setPassword("encoded_pass");
        mockUser.setFullName("Sinh Vien 01");
        mockUser.setRole(Role.STUDENT);
        mockUser.setActive(true);
    }

    @Test
    void registerStudent_success() {
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("sinhvien01@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        RegisterRequest req = new RegisterRequest();
        req.setUsername("sinhvien01");
        req.setEmail("sinhvien01@example.com");
        req.setPassword("123456");
        req.setFullName("Sinh Vien 01");

        UserDTO result = userService.registerStudent(req);

        assertNotNull(result);
        assertEquals("sinhvien01", result.getUsername());
        assertEquals(Role.STUDENT, result.getRole());
    }

    @Test
    void registerStudent_duplicateUsername_throwsException() {
        when(userRepository.findByUsername("sinhvien01")).thenReturn(Optional.of(mockUser));

        RegisterRequest req = new RegisterRequest();
        req.setUsername("sinhvien01");
        req.setEmail("other@example.com");
        req.setPassword("123456");
        req.setFullName("Test");

        assertThrows(DuplicateResourceException.class, () -> userService.registerStudent(req));
    }

    @Test
    void deactivateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.deactivateUser(1L);

        assertFalse(mockUser.isActive());
        verify(userRepository, times(1)).save(mockUser);
    }
}
