package com.example.account.modules.facturation.dto.response;

import com.example.account.modules.facturation.model.enums.ModeReglement;
import com.example.account.modules.facturation.model.enums.StatutProforma;
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
public class ProformaInvoiceResponse {
    private UUID idProformaInvoice;
    private String numeroProformaInvoice;
    private LocalDateTime dateCreation;
    private String type;
    private StatutProforma statut;
    private BigDecimal montantTotal;
    private UUID idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;
    private List<LigneProformaResponse> lignes;
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private BigDecimal tauxChange;
    private String conditionsPaiement;
    private String notes;
    private String referenceExterne;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime dateAcceptation;
    private LocalDateTime dateRefus;
    private String motifRefus;
    private UUID idFactureConvertie;
    private BigDecimal remiseGlobalePourcentage;
    private BigDecimal remiseGlobaleMontant;
    private Integer validiteOffreJours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Added fields from frontend
    private Boolean applyVat;
    private LocalDate dateSysteme;
    private ModeReglement modeReglement;
    private String nosRef;
    private String vosRef;
    private Integer nbreEcheance;
    private UUID referalClientId;
    private BigDecimal finalAmount;
     private UUID organizationId;

}
