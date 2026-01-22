package com.example.account.modules.facturation.dto.response;

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
public class BonCommandeResponse {

    private UUID idBonCommande;
    private String numeroCommande;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private UUID idFournisseur;
    private String nomFournisseur;
    private BigDecimal montantTotal;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private String devise;
    private BigDecimal tauxChange;
    private String statut;
    private String referenceExterne;
    private String conditionsPaiement;
    private Integer delaiLivraison;
    private String adresseLivraison;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime validatedAt;
    private String validatedBy;
}
