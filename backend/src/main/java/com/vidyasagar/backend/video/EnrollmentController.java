package com.vidyasagar.backend.video;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;

    // Student sees all their enrolled courses
    @GetMapping("/my")
    public ResponseEntity<List<Enrollment>> getMyEnrollments(
            @AuthenticationPrincipal String studentId) {
        return ResponseEntity.ok(
                enrollmentRepository.findByStudentId(studentId));
    }

    // Check if student is enrolled in a specific course
    @GetMapping("/check/{courseId}")
    public ResponseEntity<Boolean> checkEnrollment(
            @AuthenticationPrincipal String studentId,
            @PathVariable String courseId) {
        return ResponseEntity.ok(
                enrollmentRepository.existsByStudentIdAndCourseId(
                        studentId, courseId));
    }
}