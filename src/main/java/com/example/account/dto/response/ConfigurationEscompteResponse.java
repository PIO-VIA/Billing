package com.example.account
.dto.response;

import com.example.account
.model.enums.TypeEscompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationEscompteResponse {

    private UUID idConfiguration;

    private String nomConfiguration;

    private TypeEscompte typeEscompte;

    private BigDecimal tauxEscompte;

    private BigDecimal montantFixeEscompte;

    private Integer nombreJoursAvance;

    private BigDecimal montantMinimal;

    private BigDecimal montantMaximal;

    private LocalDate dateDebutValidite;

    private LocalDate dateFinValidite;

    private Boolean automatique;

    private Boolean cumulable;

    private Boolean actif;

    private String description;

    private String conditionsSpeciales;

    private Integer priorite;

    private Integer nombreUtilisations;

    private BigDecimal montantTotalEscompte;

    private BigDecimal economiesMoyennes;

    private LocalDateTime dernierUtilisation;

    private String statut;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}