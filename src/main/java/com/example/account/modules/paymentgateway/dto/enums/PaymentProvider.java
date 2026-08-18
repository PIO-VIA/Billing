package com.example.account.modules.paymentgateway.dto.enums;

/**
 * Mirrors Kernel's InitiatePaymentRequest.provider enum exactly
 * (kernel-core.yowyob.com /api/payments/orders).
 */
public enum PaymentProvider {
    MYCOOLPAY,
    STRIPE
}
