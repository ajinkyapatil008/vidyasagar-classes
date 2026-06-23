package com.vidyasagar.backend.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "Course ID is required")
    private String courseId;
}