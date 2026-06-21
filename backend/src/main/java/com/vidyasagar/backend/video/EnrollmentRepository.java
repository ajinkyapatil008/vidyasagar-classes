package com.vidyasagar.backend.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    boolean existsByStudentIdAndCourseId(String studentId, String courseId);
}