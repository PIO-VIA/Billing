package com.example.account.modules.paymentgateway.adapter.output.external;

import com.example.account.modules.paymentgateway.domain.port.output.PaymentGatewayPort;
import com.example.account.modules.paymentgateway.dto.request.PaymentOrderInitiateRequest;
import com.example.account.modules.paymentgateway.dto.response.PaymentOrderResponse;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Delegates payment-order initiation/status to Kernel's payment gateway
 * (kernel-core.yowyob.com /api/payments/orders) via kernelWebClient, which
 * already injects the service-account credentials/token and X-Organization-Id
 * (see KernelWebClientConfig). Account never exposes Kernel credentials to
 * the POS — the POS only ever talks to this backend.
 */
@Service
public class PaymentGatewayServiceAdapter implements PaymentGatewayPort {

    // Confirmed valid against Kernel's own /api/organizations/services/catalog.
    private static final String SERVICE_CODE = "BILLING";

    private final WebClient kernelWebClient;

    public PaymentGatewayServiceAdapter(@Qualifier("kernelWebClient") WebClient kernelWebClient) {
        this.kernelWebClient = kernelWebClient;
    }

    private static final ParameterizedTypeReference<KernelApiResponse<PaymentOrderResponse>> PAYMENT_ORDER_TYPE =
            new ParameterizedTypeReference<>() {};

    @Override
    public Mono<PaymentOrderResponse> initiateOrder(PaymentOrderInitiateRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("serviceCode", SERVICE_CODE);
        body.put("amount", request.getAmount());
        body.put("currency", request.getCurrency());
        body.put("provider", request.getProvider().name());
        body.put("method", request.getMethod().name());
        if (request.getPayerReference() != null) {
            body.put("payerReference", request.getPayerReference());
        }
        if (request.getDescription() != null) {
            body.put("description", request.getDescription());
        }

        return kernelWebClient
                .post()
                .uri("/api/payments/orders")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PAYMENT_ORDER_TYPE)
                .map(KernelApiResponse::getData);
    }

    @Override
    public Mono<PaymentOrderResponse> getOrder(String orderId) {
        return kernelWebClient
                .get()
                .uri("/api/payments/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(PAYMENT_ORDER_TYPE)
                .map(KernelApiResponse::getData);
    }
}
