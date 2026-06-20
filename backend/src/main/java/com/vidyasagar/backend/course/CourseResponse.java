package com.vidyasagar.backend.course;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CourseResponse {
    private String id;
    private String teacherId;
    private String teacherName;
    private String title;
    private String description;
    private String subject;
    private Integer classLevel;
    private BigDecimal price;
    private String thumbnailUrl;
    private String status;
    private int totalLessons;
    private Instant createdAt;
    private Instant publishedAt;
    private List<LessonResponse> lessons;
}