package com.vidyasagar.backend.video;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final StorageService storageService;

    // TEACHER uploads a video file and links it to a lesson
    @PostMapping(value = "/upload/{lessonId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @AuthenticationPrincipal String teacherId,
            @PathVariable String lessonId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        return ResponseEntity.ok(
                videoService.uploadAndLink(teacherId, lessonId, file));
    }

    // STUDENT (or anyone authenticated) requests a stream URL for a lesson
    // Returns 403 if not enrolled and not a free preview
    @GetMapping("/{lessonId}/stream")
    public ResponseEntity<StreamUrlResponse> getStreamUrl(
            @AuthenticationPrincipal String studentId,
            @PathVariable String lessonId) {

        return ResponseEntity.ok(
                videoService.getStreamUrl(studentId, lessonId));
    }

    // Serves the actual video file bytes
    // This endpoint itself is public (the "security" is that you need
    // the videoKey, which only getStreamUrl() reveals after access check)
    @GetMapping("/raw/{videoKey}")
    public ResponseEntity<Resource> serveVideo(
            @PathVariable String videoKey) {

        Path videoPath = storageService.getVideoPath(videoKey);
        Resource resource = new FileSystemResource(videoPath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(resource);
    }
}