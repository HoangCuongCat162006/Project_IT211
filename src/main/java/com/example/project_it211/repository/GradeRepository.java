package com.example.project_it211.repository;

import com.example.project_it211.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findBySubmissionId(Long submissionId);

    boolean existsBySubmissionId(Long submissionId);

    @Query("SELECT AVG(g.score) FROM Grade g")
    Double findAverageScore();

    @Query("SELECT COUNT(g) FROM Grade g")
    long countGraded();
}

