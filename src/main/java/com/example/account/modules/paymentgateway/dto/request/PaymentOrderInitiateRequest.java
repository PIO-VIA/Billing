package com.example.account.modules.paymentgateway.dto.request;

import com.example.account.modules.paymentgateway.dto.enums.PaymentMethodType;
import com.example.account.modules.paymentgateway.dto.enums.PaymentProvider;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderInitiateRequest {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotNull(message = "La devise est obligatoire")
    private String currency;

    @NotNull(message = "Le fournisseur de paiement est obligatoire")
    private PaymentProvider provider;

    @NotNull(message = "La méthode de paiement est obligatoire")
    private PaymentMethodType method;

    private String payerReference;

    private String description;
}
