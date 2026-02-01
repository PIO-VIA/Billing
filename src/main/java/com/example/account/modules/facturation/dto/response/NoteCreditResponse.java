package com.example.account.modules.facturation.dto.response;

import com.example.account.modules.facturation.model.enums.ModeReglementNoteCredit;
import com.example.account.modules.facturation.model.enums.StatutNoteCredit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteCreditResponse {
    private UUID idCNoteCredit;
    private String numeroNoteCredit;
    private String numeroFacture;
    private LocalDateTime dateFacturation;
    private LocalDateTime dateEcheance;
    private LocalDateTime dateSysteme;
    private StatutNoteCredit etat;
    private String type; 
    private String idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;
    private BigDecimal montantHT;      
    private BigDecimal montantTVA;     
    private BigDecimal montantTTC;
    private BigDecimal montantTotal;
    private BigDecimal montantRestant;
    private BigDecimal finalAmount;
    private BigDecimal remiseGlobalePourcentage;
    private BigDecimal remiseGlobaleMontant;
    private Boolean applyVat;
    private String devise;
    private BigDecimal tauxChange;
    private ModeReglementNoteCredit modeReglement;
    private String conditionsPaiement;
    private Integer nbreEcheance;
    private String nosRef;
    private String vosRef;
    private String referenceCommande;
    private UUID idDevisOrigine;
    private List<LigneFactureResponse> lignesFacture; 
    private String notes;
    private String pdfPath;
    private Boolean envoyeParEmail;
    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String referalClientId;
}
