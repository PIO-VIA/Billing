package com.example.account
.dto.request;

import com.example.account
.model.enums.TypeEcheance;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcheancePaiementCreateRequest {

    private UUID idFacture;

    @NotNull(message = "Le numéro d'échéance est obligatoire")
    private Integer numeroEcheance;

    @NotNull(message = "Le montant de l'échéance est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montantEcheance;

    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;

    private TypeEcheance typeEcheance;

    private String description;

    @Builder.Default
    private Boolean escompteAutorise = false;

    private BigDecimal tauxPenaliteRetard;

    private Integer delaiGraceJours;

    private String conditionsSpeciales;

    private String modePaiementSouhaite;

    private String commentaire;
}