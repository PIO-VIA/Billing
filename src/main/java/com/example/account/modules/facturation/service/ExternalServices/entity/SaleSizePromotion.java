package com.example.account.modules.facturation.service.ExternalServices.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleSizePromotion {

    private String saleSize;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal promotionalPrice;

    private BigDecimal discountPercentage;

    private Boolean active;
}