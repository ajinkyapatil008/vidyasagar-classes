package com.vidyasagar.backend.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // All published courses — for student browse page
    List<Course> findByStatus(CourseStatus status);

    // Filter by subject
    List<Course> findByStatusAndSubject(CourseStatus status, Subject subject);

    // Filter by class level
    List<Course> findByStatusAndClassLevel(CourseStatus status,
                                           Integer classLevel);

    // Teacher sees their own courses
    List<Course> findByTeacherId(String teacherId);
}