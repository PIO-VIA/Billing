package com.example.account.modules.facturation.dto.response;


import com.example.account.modules.facturation.model.enums.StatutFacture;
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
public class FactureResponse {

    private UUID idFacture;
    private String numeroFacture;
    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
    private String type;
    private StatutFacture etat;
    private BigDecimal montantTotal;
    private BigDecimal montantRestant;
    private UUID idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;
    private List<LigneFactureResponse> lignesFacture;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private BigDecimal tauxChange;
    private String conditionsPaiement;
    private String notes;
    private String referenceCommande;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;
    
    /**
     * Organization ID - for multi-tenancy context.
     */
    private UUID organizationId;
    
    /**
     * Global discount percentage (0-100).
     */
    private BigDecimal remiseGlobalePourcentage;
    
    /**
     * Global discount amount.
     */
    private BigDecimal remiseGlobaleMontant;
    
    /**
     * User who created this invoice.
     */
    private UUID createdBy;
    
    /**
     * Username of the user who created this invoice.
     */
    private String createdByUsername;
    
    /**
     * User who validated this invoice.
     */
    private UUID validatedBy;
    
    /**
     * Username of the user who validated this invoice.
     */
    private String validatedByUsername;
    
    /**
     * Timestamp when invoice was validated.
     */
    private LocalDateTime validatedAt;
    
    /**
     * Optimistic locking version.
     */
    private Long version;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}