package com.example.project_it211.aop;

import com.example.project_it211.dto.GradeDTO;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @AfterReturning(
            pointcut = "execution(* com.example.project_it211.service.GradeService.gradeSubmission(..))",
            returning = "result"
    )
    public void logGradingSuccess(Object result) {
        if (result instanceof GradeDTO) {
            GradeDTO grade = (GradeDTO) result;
            System.out.println("[INFO] Lecturer ID: " + grade.getLecturerId() +
                    " graded Submission ID: " + grade.getSubmissionId() +
                    " with Score: " + grade.getScore());
        }
    }

    @AfterThrowing(
            pointcut = "execution(* com.example.project_it211.service.GradeService.gradeSubmission(..))",
            throwing = "ex"
    )
    public void logGradingError(Exception ex) {
        System.err.println("[ERROR] Lecturer grading failed: " + ex.getMessage());
    }
}
