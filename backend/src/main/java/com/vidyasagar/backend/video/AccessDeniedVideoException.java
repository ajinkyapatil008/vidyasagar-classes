package com.vidyasagar.backend.video;

public class AccessDeniedVideoException extends RuntimeException {
    public AccessDeniedVideoException(String message) {
        super(message);
    }
}