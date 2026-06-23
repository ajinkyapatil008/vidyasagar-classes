package com.vidyasagar.backend.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // STUDENT creates a payment order for a course
    @PostMapping("/create-order")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @AuthenticationPrincipal String studentId,
            @RequestBody @Valid CreateOrderRequest request) {

        return ResponseEntity.ok(
                paymentService.createOrder(studentId, request));
    }

    // Razorpay calls this after payment — must be PUBLIC (no JWT)
    // Razorpay sends its own signature for verification instead
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Webhook received from Razorpay");
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok(Map.of("status", "processed"));
    }
}