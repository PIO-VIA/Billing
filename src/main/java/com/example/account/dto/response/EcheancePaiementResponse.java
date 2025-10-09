package com.example.account
.dto.response;

import com.example.account
.model.enums.StatutEcheance;
import com.example.account
.model.enums.TypeEcheance;
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
public class EcheancePaiementResponse {

    private UUID idEcheance;

    private UUID idFacture;

    private String numeroFacture;

    private Integer numeroEcheance;

    private BigDecimal montantEcheance;

    private BigDecimal montantPaye;

    private BigDecimal montantRestant;

    private LocalDate dateEcheance;

    private LocalDate datePaiementEffectif;

    private StatutEcheance statut;

    private TypeEcheance typeEcheance;

    private String description;

    private Boolean escompteDisponible;

    private BigDecimal montantEscompteApplique;

    private BigDecimal tauxEscompteApplique;

    private BigDecimal penalitesCalculees;

    private BigDecimal tauxPenaliteRetard;

    private Integer joursRetard;

    private Integer delaiGraceJours;

    private String conditionsSpeciales;

    private String modePaiementSouhaite;

    private String modePaiementEffectif;

    private String referencePaiement;

    private String commentaire;

    private UUID idClient;

    private String nomClient;

    private String emailClient;

    private Boolean rappelEnvoye;

    private LocalDate dateRappel;

    private Integer nombreRappels;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private UUID validePar;

    private LocalDateTime dateValidation;
}