package com.example.project_it211.repository;

import com.example.project_it211.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.project_it211.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    //tìm user theo username
    Optional<User> findByUsername(String username);
    //tìm user theo email
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:role IS NULL OR u.role = :role) " +
           "AND (:active IS NULL OR u.active = :active)")
    Page<User> searchUsers(@Param("search") String search,
                           @Param("role") Role role,
                           @Param("active") Boolean active,
                           Pageable pageable);
}
