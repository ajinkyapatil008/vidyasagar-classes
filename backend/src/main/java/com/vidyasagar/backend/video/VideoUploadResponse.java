package com.vidyasagar.backend.video;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoUploadResponse {
    private String videoKey;
    private String message;
}