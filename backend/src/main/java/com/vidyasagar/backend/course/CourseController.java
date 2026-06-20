package com.vidyasagar.backend.course;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // TEACHER only — create a new course
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> createCourse(
            @AuthenticationPrincipal String teacherId,
            @RequestBody @Valid CreateCourseRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.createCourse(teacherId, request));
    }

    // TEACHER only — add lesson to course
    @PostMapping("/{courseId}/lessons")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LessonResponse> addLesson(
            @AuthenticationPrincipal String teacherId,
            @PathVariable String courseId,
            @RequestBody @Valid AddLessonRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseService.addLesson(teacherId, courseId, request));
    }

    // TEACHER only — publish course
    @PatchMapping("/{courseId}/publish")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> publishCourse(
            @AuthenticationPrincipal String teacherId,
            @PathVariable String courseId) {

        return ResponseEntity.ok(
                courseService.publishCourse(teacherId, courseId));
    }

    // PUBLIC — browse all published courses
    // Optional filters: ?subject=MATHS or ?classLevel=5
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllPublished(
            @RequestParam(required = false) Subject subject,
            @RequestParam(required = false) Integer classLevel) {

        return ResponseEntity.ok(
                courseService.getAllPublished(subject, classLevel));
    }

    // PUBLIC — get single course with lessons
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable String courseId) {

        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    // TEACHER only — see my own courses (draft + published)
    @GetMapping("/my")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @AuthenticationPrincipal String teacherId) {

        return ResponseEntity.ok(courseService.getMyCourses(teacherId));
    }
}