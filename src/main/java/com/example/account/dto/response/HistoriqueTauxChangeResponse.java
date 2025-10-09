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
public class HistoriqueTauxChangeResponse {

    private UUID idHistorique;

    private String deviseSource;

    private String deviseCible;

    private BigDecimal tauxChange;

    private BigDecimal tauxPrecedent;

    private LocalDateTime dateApplication;

    private LocalDateTime dateMiseAJour;

    private String sourceTaux;

    private Boolean automatique;

    private Boolean actif;

    private String description;

    private BigDecimal variationPourcentage;

    private BigDecimal commissionPourcentage;

    private BigDecimal commissionFixe;

    private BigDecimal seuilAlerte;

    private String fournisseurApi;

    private String referenceTaux;

    private String statut;

    private Integer nombreUtilisations;

    private BigDecimal volumeConversions;

    private LocalDateTime dernierUtilisation;

    private String commentaire;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}