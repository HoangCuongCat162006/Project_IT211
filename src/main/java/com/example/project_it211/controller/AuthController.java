package com.example.project_it211.controller;

import com.example.project_it211.dto.*;
import com.example.project_it211.service.AuthService;
import com.example.project_it211.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    // Đăng ký sinh viên mới (public)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO userDTO = userService.registerStudent(request);
        return new ResponseEntity<>(
                ApiResponse.success("Đăng ký tài khoản sinh viên thành công", userDTO),
                HttpStatus.CREATED);
    }

    // Đăng nhập (public)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    // Làm mới access token (public)
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Lấy access token mới thành công", response));
    }

    // Đăng xuất (cần đăng nhập)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    // =======================================================================
    // FR-10: Quên mật khẩu — BƯỚC 1: gửi OTP về email
    // =======================================================================
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userService.sendOtpForPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success(
                "OTP đã được gửi tới email của bạn. Mã có hiệu lực trong 5 phút.",
                Map.of("email", request.getEmail())
        ));
    }

    // =======================================================================
    // FR-10: Quên mật khẩu — BƯỚC 2: nhập OTP + đặt mật khẩu mới
    // =======================================================================
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPasswordWithOtp(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Đặt lại mật khẩu thành công. Vui lòng đăng nhập với mật khẩu mới.", null));
    }

    // Đổi mật khẩu (cần đăng nhập)
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        if (principal == null) throw new IllegalArgumentException("Bạn cần đăng nhập để đổi mật khẩu");
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}
