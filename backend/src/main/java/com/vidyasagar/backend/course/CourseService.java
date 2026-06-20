package com.vidyasagar.backend.course;

import com.vidyasagar.backend.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    // TEACHER creates a new course in DRAFT status
    public CourseResponse createCourse(String teacherId,
                                       CreateCourseRequest request) {

        String teacherName = userRepository.findById(teacherId)
                .map(u -> u.getName())
                .orElse("Unknown Teacher");

        Course course = Course.builder()
                .teacherId(teacherId)
                .teacherName(teacherName)
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(request.getSubject())
                .classLevel(request.getClassLevel())
                .price(request.getPrice())
                .thumbnailUrl(request.getThumbnailUrl())
                .status(CourseStatus.DRAFT)
                .build();

        Course saved = courseRepository.save(course);
        return toResponse(saved, List.of());
    }

    // TEACHER adds a lesson to their course
    @Transactional
    public LessonResponse addLesson(String teacherId,
                                    String courseId,
                                    AddLessonRequest request) {

        Course course = getCourseOwnedByTeacher(courseId, teacherId);

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .videoKey(request.getVideoKey())
                .durationSeconds(request.getDurationSeconds())
                .orderIndex(request.getOrderIndex())
                .isFreePreview(request.getIsFreePreview() != null
                        && request.getIsFreePreview())
                .build();

        Lesson saved = lessonRepository.save(lesson);
        return toLessonResponse(saved);
    }

    // TEACHER publishes their course
    @Transactional
    public CourseResponse publishCourse(String teacherId, String courseId) {

        Course course = getCourseOwnedByTeacher(courseId, teacherId);

        int lessonCount = lessonRepository.countByCourseId(courseId);
        if (lessonCount == 0) {
            throw new RuntimeException(
                    "Cannot publish course with no lessons. Add at least one lesson first.");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(Instant.now());

        Course saved = courseRepository.save(course);
        List<LessonResponse> lessons = lessonRepository
                .findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream().map(this::toLessonResponse).toList();

        return toResponse(saved, lessons);
    }

    // PUBLIC — browse all published courses
//    public List<CourseResponse> getAllPublished(Subject subject,
//                                                Integer classLevel) {
//        List<Course> courses;
//
//        if (subject != null) {
//            courses = courseRepository.findByStatusAndSubject(
//                    CourseStatus.PUBLISHED, subject);
//        } else if (classLevel != null) {
//            courses = courseRepository.findByStatusAndClassLevel(
//                    CourseStatus.PUBLISHED, classLevel);
//        } else {
//            courses = courseRepository.findByStatus(CourseStatus.PUBLISHED);
//        }
//
//        return courses.stream()
//                .map(c -> toResponse(c, List.of()))
//                .toList();
//    }
    // PUBLIC — browse all published courses
    public List<CourseResponse> getAllPublished(Subject subject,
                                                Integer classLevel) {
        List<Course> courses;

        if (subject != null) {
            courses = courseRepository.findByStatusAndSubject(
                    CourseStatus.PUBLISHED, subject);
        } else if (classLevel != null) {
            courses = courseRepository.findByStatusAndClassLevel(
                    CourseStatus.PUBLISHED, classLevel);
        } else {
            courses = courseRepository.findByStatus(CourseStatus.PUBLISHED);
        }

        return courses.stream()
                .map(c -> {
                    int count = lessonRepository.countByCourseId(c.getId());
                    CourseResponse response = toResponse(c, List.of());
                    response.setTotalLessons(count);
                    return response;
                })
                .toList();
    }

    // PUBLIC — get course detail with all lessons
    public CourseResponse getCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<LessonResponse> lessons = lessonRepository
                .findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream().map(this::toLessonResponse).toList();

        return toResponse(course, lessons);
    }

    // TEACHER — see their own courses
    public List<CourseResponse> getMyCourses(String teacherId) {
        return courseRepository.findByTeacherId(teacherId)
                .stream()
                .map(c -> toResponse(c, List.of()))
                .toList();
    }

    // ---- Helper methods ----

    private Course getCourseOwnedByTeacher(String courseId, String teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException(
                    "You don't have permission to modify this course");
        }
        return course;
    }

    private CourseResponse toResponse(Course course,
                                      List<LessonResponse> lessons) {
        return CourseResponse.builder()
                .id(course.getId())
                .teacherId(course.getTeacherId())
                .teacherName(course.getTeacherName())
                .title(course.getTitle())
                .description(course.getDescription())
                .subject(course.getSubject().name())
                .classLevel(course.getClassLevel())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .status(course.getStatus().name())
                .totalLessons(lessons.size())
                .createdAt(course.getCreatedAt())
                .publishedAt(course.getPublishedAt())
                .lessons(lessons)
                .build();
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .durationSeconds(lesson.getDurationSeconds())
                .orderIndex(lesson.getOrderIndex())
                .isFreePreview(lesson.getIsFreePreview())
                .build();
    }
}