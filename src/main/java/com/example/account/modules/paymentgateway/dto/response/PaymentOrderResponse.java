package com.example.account.modules.paymentgateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Mirrors Kernel's own PaymentOrderResponse shape (kernel-core.yowyob.com
 * /api/payments/orders) — status is a bare string on Kernel's side too (no
 * enum published), only "PENDING" confirmed live so far; sibling checkout
 * flows in the same Kernel API (wallet recharge, service-bundle checkout)
 * use PENDING_PAYMENT/ACTIVE/FAILED/CANCELLED, which is the closest
 * reference we have for what the terminal values might look like here.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {
    private String id;
    private String provider;
    private String method;
    private String status;
    private String providerReference;
    private String redirectUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
