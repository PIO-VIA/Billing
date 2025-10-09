package com.example.account
.dto.response;

import com.example.account
.model.enums.TypeEscompte;
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
public class CalculEscompteResponse {

    private UUID idEcheance;

    private UUID idConfiguration;

    private String nomConfiguration;

    private BigDecimal montantOriginal;

    private BigDecimal montantEscompte;

    private BigDecimal montantAPayer;

    private BigDecimal tauxEscompteApplique;

    private Integer joursAvance;

    private Boolean escompteApplicable;

    private String motifNonApplicable;

    private TypeEscompte typeEscompte;

    private BigDecimal economieRealisee;

    private BigDecimal pourcentageEconomie;

    private LocalDateTime dateCalcul;

    private LocalDateTime dateValidite;

    private Boolean garantiTaux;

    private Integer dureeGarantieMinutes;

    private String conditionsApplication;

    private String commentaire;

    private BigDecimal montantCommission;

    private String referenceCalcul;

    private Boolean cumulAvecAutresEscomptes;

    private BigDecimal plafondEscompte;

    private String statutCalcul; // VALIDE, EXPIRE, ANNULE

    private LocalDateTime createdAt;
}