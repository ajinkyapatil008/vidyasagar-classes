package com.vidyasagar.backend.payment;

public enum PaymentStatus {
    PENDING,    // Order created, payment not yet done
    SUCCESS,    // Payment confirmed via webhook
    FAILED      // Payment failed or expired
}