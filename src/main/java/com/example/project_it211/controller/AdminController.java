package com.example.project_it211.controller;

import com.example.project_it211.dto.ApiResponse;
import com.example.project_it211.dto.CourseDTO;
import com.example.project_it211.dto.UserDTO;
import com.example.project_it211.entity.Role;
import com.example.project_it211.service.CourseService;
import com.example.project_it211.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    // --- QUẢN LÝ NGƯỜI DÙNG (USERS) ---

    // Tìm kiếm, phân trang và lọc người dùng (FR-05)
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserDTO> users = userService.getUsers(search, role, active, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", users));
    }

    // Tạo người dùng mới (FR-05)
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        UserDTO created = userService.createUser(userDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Tạo người dùng mới thành công", created),
                HttpStatus.CREATED
        );
    }

    // Cập nhật người dùng (FR-05)
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin người dùng thành công", updated));
    }

    // Vô hiệu hóa người dùng (FR-05)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    // --- QUẢN LÝ LỚP HỌC / KHÓA HỌC (COURSES) ---

    // Tìm kiếm, phân trang và lọc khóa học (FR-05)
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseDTO> courses = courseService.getCourses(search, active, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", courses));
    }

    // Tạo khóa học mới (FR-05)
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO created = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Tạo khóa học mới thành công", created),
                HttpStatus.CREATED
        );
    }

    // Cập nhật khóa học (FR-05)
    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseDTO courseDTO) {
        CourseDTO updated = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin khóa học thành công", updated));
    }

    // Vô hiệu hóa khóa học (FR-05)
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deactivateCourse(@PathVariable Long id) {
        courseService.deactivateCourse(id);
        return ResponseEntity.noContent().build();
    }
}
