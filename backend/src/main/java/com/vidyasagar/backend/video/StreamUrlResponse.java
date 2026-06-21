package com.vidyasagar.backend.video;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreamUrlResponse {
    private String streamUrl;
    private long expiresInSeconds;
}