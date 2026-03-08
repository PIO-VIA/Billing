package com.example.account.modules.facturation.service.ExternalServices.entity;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSaleSize {

    private String size;

    private BigDecimal unitPrice;

    private BigDecimal unitPriceWithTax;

    private Integer minQuantity;

    private Boolean active;

    private Boolean isNegotiable;

    private BigDecimal minNegotiationPercentage;
}