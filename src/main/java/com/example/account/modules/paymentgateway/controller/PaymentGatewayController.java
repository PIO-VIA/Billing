package com.example.account.modules.paymentgateway.controller;

import com.example.account.modules.paymentgateway.domain.port.output.PaymentGatewayPort;
import com.example.account.modules.paymentgateway.dto.request.PaymentOrderInitiateRequest;
import com.example.account.modules.paymentgateway.dto.response.PaymentOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments/orders")
@RequiredArgsConstructor
@Tag(name = "PaymentGateway", description = "Proxy for Kernel's card / mobile-money payment gateway (WebFlux)")
public class PaymentGatewayController {

    private final PaymentGatewayPort paymentGatewayPort;

    @PostMapping
    @Operation(summary = "Initier un paiement (carte ou mobile money)")
    public Mono<ResponseEntity<PaymentOrderResponse>> initiateOrder(@Valid @RequestBody PaymentOrderInitiateRequest request) {
        return paymentGatewayPort.initiateOrder(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer le statut d'un paiement — utilisé par le POS pour le polling")
    public Mono<ResponseEntity<PaymentOrderResponse>> getOrder(@PathVariable String id) {
        return paymentGatewayPort.getOrder(id)
                .map(ResponseEntity::ok);
    }
}
