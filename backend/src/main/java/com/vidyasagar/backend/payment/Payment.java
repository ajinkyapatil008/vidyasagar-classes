package com.vidyasagar.backend.payment;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Who is paying
    @Column(nullable = false)
    private String studentId;

    // What they're paying for
    @Column(nullable = false)
    private String courseId;

    // Razorpay's order ID — returned when we create an order
    @Column(unique = true)
    private String razorpayOrderId;

    // Razorpay's payment ID — only available after successful payment
    private String razorpayPaymentId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant paidAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}