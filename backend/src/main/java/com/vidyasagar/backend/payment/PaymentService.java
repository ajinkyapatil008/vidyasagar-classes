package com.vidyasagar.backend.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.vidyasagar.backend.course.Course;
import com.vidyasagar.backend.course.CourseRepository;
import com.vidyasagar.backend.video.Enrollment;
import com.vidyasagar.backend.video.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    // Step 1 — Student initiates payment for a course
    public CreateOrderResponse createOrder(String studentId,
                                           CreateOrderRequest request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if student already paid for this course
        boolean alreadyPaid = paymentRepository
                .existsByStudentIdAndCourseIdAndStatus(
                        studentId, course.getId(), PaymentStatus.SUCCESS);
        if (alreadyPaid) {
            throw new RuntimeException(
                    "You have already purchased this course");
        }

        // Create Razorpay order
        try {
            long amountInPaise = course.getPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "vidyasagar_" + studentId
                    .substring(0, 8));
            orderRequest.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            // Save pending payment record
            Payment payment = Payment.builder()
                    .studentId(studentId)
                    .courseId(course.getId())
                    .razorpayOrderId(razorpayOrderId)
                    .amount(course.getPrice())
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);

            log.info("Created Razorpay order {} for student {} course {}",
                    razorpayOrderId, studentId, course.getId());

            return CreateOrderResponse.builder()
                    .razorpayOrderId(razorpayOrderId)
                    .currency("INR")
                    .amount(course.getPrice())
                    .amountInPaise(amountInPaise)
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .keyId(keyId)
                    .build();

        } catch (RazorpayException e) {
            throw new RuntimeException(
                    "Failed to create payment order: " + e.getMessage());
        }
    }

    // Step 2 — Razorpay calls this webhook after payment completes
    @Transactional
    public void handleWebhook(String payload, String razorpaySignature) {

        // SECURITY — verify the webhook is actually from Razorpay
        if (!isValidSignature(payload, razorpaySignature)) {
            log.warn("Invalid webhook signature received — ignoring");
            throw new RuntimeException("Invalid webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        log.info("Received Razorpay webhook event: {}", eventType);

        // Only process successful payments
        if (!"payment.captured".equals(eventType)) {
            log.info("Ignoring event type: {}", eventType);
            return;
        }

        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId  = paymentEntity.getString("order_id");
        String razorpayPaymentId = paymentEntity.getString("id");

        // Find our payment record by Razorpay order ID
        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> {
                    log.error("No payment found for order {}", razorpayOrderId);
                    return new RuntimeException("Payment record not found");
                });

        // Avoid double-processing the same webhook
        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            log.info("Payment {} already processed — skipping", razorpayOrderId);
            return;
        }

        // Mark payment as successful
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setPaidAt(Instant.now());
        paymentRepository.save(payment);

        // Create enrollment — this is what unlocks the course for the student
        boolean alreadyEnrolled = enrollmentRepository
                .existsByStudentIdAndCourseId(
                        payment.getStudentId(), payment.getCourseId());

        if (!alreadyEnrolled) {
            Enrollment enrollment = Enrollment.builder()
                    .studentId(payment.getStudentId())
                    .courseId(payment.getCourseId())
                    .build();
            enrollmentRepository.save(enrollment);

            log.info("Enrollment created for student {} in course {}",
                    payment.getStudentId(), payment.getCourseId());
        }
    }

    // HMAC-SHA256 signature verification
    private boolean isValidSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hash);
            return computed.equals(signature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }
}