package com.example.account.dto.response;

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
public class BonAchatResponse {

    private UUID idBonAchat;
    private String numeroBon;
    private LocalDate dateAchat;
    private LocalDate dateLivraison;
    private UUID idFournisseur;
    private String nomFournisseur;
    private UUID idBonCommande;
    private String numeroCommandeRef;
    private BigDecimal montantTotal;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private String devise;
    private BigDecimal tauxChange;
    private String statut;
    private String numeroFactureFournisseur;
    private String modePaiement;
    private String conditionsPaiement;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime validatedAt;
    private String validatedBy;
}
