package com.example.project_it211.aop;

import com.example.project_it211.dto.GradeDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * FR-11 - AF1: Ghi log thời gian thực hiện cho tất cả các chức năng.
 *
 * Aspect này sử dụng kỹ thuật AOP để:
 * 1. (@Around)          Đo và ghi thời gian thực hiện của TOÀN BỘ các method trong service layer.
 * 2. (@AfterReturning)  Ghi log chi tiết khi chấm điểm thành công.
 * 3. (@AfterThrowing)   Ghi log lỗi khi chấm điểm thất bại.
 *
 * Không viết bất kỳ dòng log nào trực tiếp trong tầng Service / Controller.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // =========================================================================
    // AF1: Ghi log thời gian thực hiện cho TẤT CẢ các method trong service layer
    // =========================================================================

    /**
     * Pointcut bao phủ toàn bộ các method trong package service:
     *   - AuthService, UserService, CourseService, EnrollmentService,
     *     GradeService, SubmissionService, LectureMaterialService, CloudinaryService
     */
    @Around("execution(* com.example.project_it211.controller..*(..)) || " +
            "execution(* com.example.project_it211.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className  = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("[PERF] {}.{}() executed in {} ms",
                    className, methodName, executionTime);

            return result;

        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("[PERF] {}.{}() failed after {} ms | Error: {}",
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    // =========================================================================
    // UC-04: Ghi log chi tiết kết quả chấm điểm (AfterReturning / AfterThrowing)
    // =========================================================================

    /**
     * Sau khi gradeSubmission() trả về thành công,
     * ghi log: Lecturer ID, Submission ID, Score.
     */
    @AfterReturning(
            pointcut = "execution(* com.example.project_it211.service.GradeService.gradeSubmission(..))",
            returning = "result"
    )
    public void logGradingSuccess(Object result) {
        if (result instanceof GradeDTO grade) {
            logger.info("[INFO] Lecturer ID: {} graded Submission ID: {} with Score: {}",
                    grade.getLecturerId(),
                    grade.getSubmissionId(),
                    grade.getScore());
        }
    }

    /**
     * Sau khi gradeSubmission() ném ngoại lệ,
     * ghi log lỗi để phục vụ giám sát và debug.
     */
    @AfterThrowing(
            pointcut = "execution(* com.example.project_it211.service.GradeService.gradeSubmission(..))",
            throwing = "ex"
    )
    public void logGradingError(Exception ex) {
        logger.error("[ERROR] Lecturer grading failed: {}", ex.getMessage());
    }
}