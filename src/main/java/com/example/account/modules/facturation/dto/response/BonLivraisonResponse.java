package com.example.account.modules.facturation.dto.response;

import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonLivraisonResponse {
    private UUID idBonLivraison;
    private String numeroBonLivraison;
    
    // Receiver Information
    private String nomDestinataire;
    private String adresseDestinataire;
    private String contactDestinataire;

    // Agency / Pickup Address
    private String nomAgence;
    private String adresseAgence;
    private String contactAgence;

    private LocalDate dateLivraison;
    private LocalDate dateEcheance;
    
    private List<LigneBonLivraisonResponse> lines;
    
    private BigDecimal totalAmount;
    private String termsAndConditions;
    private String purchaseOrderNumber;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Extra fields from entity for completeness
    private UUID idClient;
    private String nomClient;
    private StatutBonLivraison statut;
    private String transporteur;
    private String numeroSuivi;
    private UUID organizationId;
}
