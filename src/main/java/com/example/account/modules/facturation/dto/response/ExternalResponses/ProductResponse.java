package com.example.account.modules.facturation.dto.response.ExternalResponses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {

    private UUID idProduit;
    private String nomProduit;
    private String typeProduit;
    private BigDecimal prixVente;
    private BigDecimal cout;
    private String categorie;
    private String reference;
    private String codeBarre;
    private String photo;
    private Boolean active;

    // ISO Format works best for the "2024-10-01T08:00:00Z" strings in your JSON
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private String uom;
    private List<ClientSaleSize> allowedSaleSizes;
    private List<SaleSizePromotion> activePromotions;
    
    private Double stockQuantity;
    private Double availableQuantity;
    private Double reservedQuantity;
    private UUID organizationId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientSaleSize {
        private String size; // Matches TypeScript 'size' enum string
        private BigDecimal unitPrice;
        private BigDecimal unitPriceWithTax;
        private Integer minQuantity;
        private Boolean active;
        private Boolean isNegotiable;
        private Double minNegotiationPercentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleSizePromotion {
        private String saleSize; // Matches 'saleSize' in JSON
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal promotionalPrice;
        private Double discountPercentage;
        private Boolean active;
    }
}