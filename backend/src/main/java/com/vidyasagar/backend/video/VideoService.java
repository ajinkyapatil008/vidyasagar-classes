package com.vidyasagar.backend.video;

import com.vidyasagar.backend.course.Course;
import com.vidyasagar.backend.course.CourseRepository;
import com.vidyasagar.backend.course.Lesson;
import com.vidyasagar.backend.course.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final StorageService storageService;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // TEACHER uploads a video and links it to a lesson
    public VideoUploadResponse uploadAndLink(String teacherId,
                                             String lessonId,
                                             MultipartFile file) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Verify the teacher owns the course this lesson belongs to
        if (!lesson.getCourse().getTeacherId().equals(teacherId)) {
            throw new RuntimeException(
                    "You don't have permission to upload to this lesson");
        }

        String videoKey = storageService.saveVideo(file);

        lesson.setVideoKey(videoKey);
        lessonRepository.save(lesson);

        return VideoUploadResponse.builder()
                .videoKey(videoKey)
                .message("Video uploaded and linked successfully")
                .build();
    }

    // Generates a stream URL — but ONLY if student is enrolled or it's a free preview
    public StreamUrlResponse getStreamUrl(String studentId, String lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (lesson.getVideoKey() == null) {
            throw new RuntimeException("No video uploaded for this lesson yet");
        }

        boolean isFreePreview = Boolean.TRUE.equals(lesson.getIsFreePreview());

        if (!isFreePreview) {
            Course course = lesson.getCourse();
            boolean enrolled = enrollmentRepository
                    .existsByStudentIdAndCourseId(studentId, course.getId());

            if (!enrolled) {
                throw new AccessDeniedVideoException(
                        "You must enroll in this course to watch this lesson");
            }
        }

        // Local disk version — direct URL with videoKey
        // (will become a signed R2 URL with expiry once we migrate to R2)
        String streamUrl = "http://localhost:8080/api/videos/raw/"
                + lesson.getVideoKey();

        return StreamUrlResponse.builder()
                .streamUrl(streamUrl)
                .expiresInSeconds(7200) // 2 hours, same contract as R2 later
                .build();
    }
}