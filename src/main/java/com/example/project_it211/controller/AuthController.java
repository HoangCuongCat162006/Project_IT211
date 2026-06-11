package com.example.project_it211.controller;

import com.example.project_it211.dto.ApiResponse;
import com.example.project_it211.dto.RegisterRequest;
import com.example.project_it211.dto.UserDTO;
import com.example.project_it211.service.UserService;
import com.example.project_it211.dto.AuthResponse;
import com.example.project_it211.dto.LoginRequest;
import com.example.project_it211.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    // đăng ký sinh viên mới
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO userDTO = userService.registerStudent(request);
        return new ResponseEntity<>(
                ApiResponse.success("Đăng ký tài khoản sinh viên thành công", userDTO),
                HttpStatus.CREATED
        );
    }

    // đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    // làm mới access token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Lấy access token mới thành công", response));
    }

    // đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }
}
