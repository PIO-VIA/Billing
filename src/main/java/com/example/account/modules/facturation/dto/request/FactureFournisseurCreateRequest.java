package com.example.account.modules.facturation.dto.request;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.example.account.modules.facturation.model.entity.Lines.LineFactureFournisseur;
import com.example.account.modules.facturation.model.enums.StatutFactureFournisseur;
import com.example.account.modules.facturation.model.enums.TypePaiementFactureFournisseur;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class FactureFournisseurCreateRequest {
     private String numeroFacture;
    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
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
    private Double remiseGlobalePourcentage;
    private Boolean applyVat;
    private String devise;
    private Double tauxChange;
    private TypePaiementFactureFournisseur modeReglement;
    private String referenceCommande;
    private UUID idGRN;
    private String numeroGRN;
    private List<LineFactureFournisseur> lignesFacture;
    private String notes;
    private UUID createdBy;
}

