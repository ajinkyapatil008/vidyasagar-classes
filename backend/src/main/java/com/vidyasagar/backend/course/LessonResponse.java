package com.vidyasagar.backend.course;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonResponse {
    private String id;
    private String title;
    private Integer durationSeconds;
    private Integer orderIndex;
    private Boolean isFreePreview;
    // videoKey is NOT included here — only returned via secure signed URL
}