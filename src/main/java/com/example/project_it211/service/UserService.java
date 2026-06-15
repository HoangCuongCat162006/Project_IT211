package com.example.project_it211.service;

import com.example.project_it211.dto.ChangePasswordRequest;
import com.example.project_it211.dto.ForgotPasswordRequest;
import com.example.project_it211.dto.RegisterRequest;
import com.example.project_it211.dto.ResetPasswordRequest;
import com.example.project_it211.dto.UserDTO;
import com.example.project_it211.entity.Role;
import com.example.project_it211.entity.User;
import com.example.project_it211.exception.DuplicateResourceException;
import com.example.project_it211.exception.ResourceNotFoundException;
import com.example.project_it211.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private OtpService otpService;
    @Autowired private EmailService emailService;

    // =====================================================================
    // FR-10: Quên mật khẩu — BƯỚC 1: gửi OTP về email
    // =====================================================================
    public void sendOtpForPasswordReset(ForgotPasswordRequest request) {
        // Tìm user theo username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản: " + request.getUsername()));

        // Kiểm tra email có khớp không
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email không trùng khớp với tài khoản");
        }

        // Kiểm tra tài khoản còn hoạt động
        if (!user.isActive()) {
            throw new IllegalArgumentException("Tài khoản đã bị vô hiệu hóa");
        }

        // Tạo OTP 6 số và lưu vào bộ nhớ
        String otp = otpService.generateAndStoreOtp(user.getEmail());

        // Gửi OTP về email (hoặc in ra console nếu chế độ demo)
        emailService.sendOtpEmail(user.getEmail(), otp, 5);
    }

    // =====================================================================
    // FR-10: Quên mật khẩu — BƯỚC 2: xác minh OTP + đặt mật khẩu mới
    // =====================================================================
    public void resetPasswordWithOtp(ResetPasswordRequest request) {
        // Kiểm tra OTP có đúng và còn hạn không
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException(
                    "Mã OTP không đúng hoặc đã hết hạn (OTP chỉ có hiệu lực 5 phút)");
        }

        // Kiểm tra 2 mật khẩu mới có khớp nhau không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không trùng khớp");
        }

        // Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + request.getEmail()));

        // Đặt mật khẩu mới (băm BCrypt)
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP đã dùng khỏi bộ nhớ
        otpService.clearOtp(request.getEmail());
    }

    // =====================================================================
    // Các method cũ giữ nguyên
    // =====================================================================

    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với username: " + username));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không trùng khớp");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Giữ lại method cũ để Postman collection FR-10 (Quên MK) vẫn chạy được
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với username: " + request.getUsername()));
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email không trùng khớp với tài khoản đã đăng ký");
        }
        String newPassword = "Reset" + (int) (Math.random() * 900000 + 100000);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return newPassword;
    }

    public UserDTO registerStudent(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new DuplicateResourceException("Username đã tồn tại");
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new DuplicateResourceException("Email đã tồn tại");
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.STUDENT);
        user.setActive(true);
        return convertToDTO(userRepository.save(user));
    }

    public Page<UserDTO> getUsers(String search, Role role, Boolean active, Pageable pageable) {
        return userRepository.searchUsers(search, role, active, pageable).map(this::convertToDTO);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
            throw new DuplicateResourceException("Username đã tồn tại");
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new DuplicateResourceException("Email đã tồn tại");
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        String rawPassword = (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) ? "123456" : userDTO.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(userDTO.getFullName());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : Role.STUDENT);
        user.setActive(userDTO.isActive());
        return convertToDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        userRepository.findByUsername(userDTO.getUsername()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new DuplicateResourceException("Username đã tồn tại");
        });
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new DuplicateResourceException("Email đã tồn tại");
        });
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        if (userDTO.getRole() != null) user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return convertToDTO(userRepository.save(user));
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        return dto;
    }
}
