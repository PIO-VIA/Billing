package com.example.account
.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionDeviseResponse {

    private UUID idConversion;

    private BigDecimal montantOriginal;

    private String deviseSource;

    private String deviseCible;

    private BigDecimal tauxUtilise;

    private BigDecimal montantConverti;

    private BigDecimal montantAvecCommission;

    private BigDecimal commissionAppliquee;

    private BigDecimal commissionPourcentage;

    private BigDecimal commissionFixe;

    private LocalDateTime dateConversion;

    private LocalDateTime dateValiditeTaux;

    private String sourceTaux;

    private String fournisseurApi;

    private String referenceTaux;

    private Boolean tauxGaranti;

    private Integer dureeGarantieMinutes;

    private String typeConversion; // SPOT, FORWARD, etc.

    private String statut; // PENDING, COMPLETED, EXPIRED, CANCELLED

    private String commentaire;

    private BigDecimal ecartTauxMarche;

    private Boolean alerteVariation;

    private String motifConversion;

    private UUID idFactureAssocie;

    private UUID idClientAssocie;

    private String numeroReference;

    private LocalDateTime createdAt;

    private String createdBy;
}