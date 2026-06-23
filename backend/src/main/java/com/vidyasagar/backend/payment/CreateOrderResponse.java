package com.vidyasagar.backend.payment;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CreateOrderResponse {
    private String razorpayOrderId;  // Pass this to Razorpay checkout
    private String currency;
    private BigDecimal amount;       // In rupees (for display)
    private long amountInPaise;      // Razorpay uses paise (multiply by 100)
    private String courseId;
    private String courseTitle;
    private String keyId;            // Frontend needs this to open Razorpay modal
}