package com.vidyasagar.backend.course;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateCourseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Subject is required")
    private Subject subject;

    @NotNull(message = "Class level is required")
    @Min(value = 1, message = "Class level must be between 1 and 10")
    @Max(value = 10, message = "Class level must be between 1 and 10")
    private Integer classLevel;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    private String thumbnailUrl;
}