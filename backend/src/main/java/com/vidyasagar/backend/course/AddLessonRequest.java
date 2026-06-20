package com.vidyasagar.backend.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddLessonRequest {

    @NotBlank(message = "Lesson title is required")
    private String title;

    // Video key from R2 upload (Week 4)
    // Optional for now — teacher can add video later
    private String videoKey;

    private Integer durationSeconds;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    private Boolean isFreePreview = false;
}