package com.vidyasagar.backend.video;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "enrollments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false, updatable = false)
    private Instant enrolledAt;

    @PrePersist
    protected void onCreate() {
        this.enrolledAt = Instant.now();
    }
}