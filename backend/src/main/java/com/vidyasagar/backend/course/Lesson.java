package com.vidyasagar.backend.course;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "lessons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @Column(nullable = false)
    private String title;

    // S3/R2 key — actual video file path in cloud storage
    private String videoKey;

    // Video length in seconds
    private Integer durationSeconds;

    // Order of lesson in the course (1, 2, 3...)
    @Column(nullable = false)
    private Integer orderIndex;

    // Free preview — student can watch without paying
    @Column(nullable = false)
    @Builder.Default
    private Boolean isFreePreview = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}