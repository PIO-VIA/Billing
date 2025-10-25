package com.example.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonCommandeUpdateRequest {

    private String numeroCommande;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private UUID idFournisseur;
    private String nomFournisseur;

    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    private BigDecimal montantTotal;

    @PositiveOrZero(message = "Le montant HT doit être positif ou nul")
    private BigDecimal montantHT;

    @PositiveOrZero(message = "Le montant TVA doit être positif ou nul")
    private BigDecimal montantTVA;

    private String devise;
    private BigDecimal tauxChange;
    private String statut;
    private String referenceExterne;
    private String conditionsPaiement;
    private Integer delaiLivraison;
    private String adresseLivraison;
    private String notes;
}
