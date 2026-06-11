package com.example.project_it211.repository;

import com.example.project_it211.entity.LectureMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {

    List<LectureMaterial> findByCourseId(Long courseId);

    Page<LectureMaterial> findByCourseId(Long courseId, Pageable pageable);
}
