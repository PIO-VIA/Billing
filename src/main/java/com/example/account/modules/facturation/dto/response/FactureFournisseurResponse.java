package com.example.account.modules.facturation.dto.response;

import lombok.*;
import com.example.account.modules.facturation.model.entity.Lines.LineFactureFournisseur;
import com.example.account.modules.facturation.model.enums.StatutFactureFournisseur;
import com.example.account.modules.facturation.model.enums.TypePaiementFactureFournisseur;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class FactureFournisseurResponse {
    private UUID idFacture;
    private String numeroFacture;
    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
    private LocalDateTime dateSysteme;
    private StatutFactureFournisseur etat;
    private String type;
    private UUID idFournisseur;
    private String nomFournisseru;
    private String adresseFournisseur;
    private String emailFournisseur;
    private String telephoneFournisseur;
    private Double montantHT;
    private Double montantTVA;
    private Double montantTTC;
    private Double montantTotal;
    private Double montantRestant;
    private Double finalAmount;
    private Double remiseGlobalePourcentage;
    private Double remiseGlobaleMontant;
    private Boolean applyVat;
    private String devise;
    private Double tauxChange;
    private TypePaiementFactureFournisseur modeReglement;
    private String conditionsPaiement;
    private Integer nbreEcheance;
    private String nosRef;
    private String vosRef;
    private String referenceCommande;
    private UUID idGRN;
    private String numeroGRN;
    private List<LineFactureFournisseur> lignesFacture;
    private String notes;
    private UUID createdBy;
    private UUID approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}