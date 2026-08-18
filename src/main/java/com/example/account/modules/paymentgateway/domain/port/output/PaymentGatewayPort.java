package com.example.account.modules.paymentgateway.domain.port.output;

import com.example.account.modules.paymentgateway.dto.request.PaymentOrderInitiateRequest;
import com.example.account.modules.paymentgateway.dto.response.PaymentOrderResponse;
import reactor.core.publisher.Mono;

/**
 * Port for delegating payment-order (card / mobile money) operations to
 * Kernel's payment gateway. Account holds no local state for these orders —
 * Kernel is the source of truth; the POS polls getOrder() for status.
 */
public interface PaymentGatewayPort {

    Mono<PaymentOrderResponse> initiateOrder(PaymentOrderInitiateRequest request);

    Mono<PaymentOrderResponse> getOrder(String orderId);
}
