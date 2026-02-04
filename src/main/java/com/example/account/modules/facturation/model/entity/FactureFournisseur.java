package com.example.account.modules.facturation.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.example.account.modules.facturation.model.entity.Lines.LineFactureFournisseur;
import com.example.account.modules.facturation.model.enums.StatutFactureFournisseur;
import com.example.account.modules.facturation.model.enums.TypePaiementFactureFournisseur;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("factures_fournisseurs")
public class FactureFournisseur {
    @Id
    @Column("id_facture")
    private UUID idFacture;

    @Column("numero_facture")
    private String numeroFacture;

    @Column("date_facturation")
    private LocalDate dateFacturation;

    @Column("date_echeance")
    private LocalDate dateEcheance;
    
    @Column("date_systeme")
    private LocalDateTime dateSysteme;

    @Column("etat")
    private StatutFactureFournisseur etat;

    @Column("type")
    private String type;
    
    // Supplier Info
    @Column("id_fournisseur")
    private UUID idFournisseur;

    @Column("nom_fournisseru") // Key typo preserved for DB consistency
    private String nomFournisseru; 

    @Column("adresse_fournisseur")
    private String adresseFournisseur;

    @Column("email_fournisseur")
    private String emailFournisseur;

    @Column("telephone_fournisseur")
    private String telephoneFournisseur;
    
    // Financials
    @Column("montant_ht")
    private Double montantHT;

    @Column("montant_tva")
    private Double montantTVA;

    @Column("montant_ttc")
    private Double montantTTC;

    @Column("montant_total")
    private Double montantTotal;

    @Column("montant_restant")
    private Double montantRestant;

    @Column("final_amount")
    private Double finalAmount;
    
    // Discounts & VAT
    @Column("remise_globale_pourcentage")
    private Double remiseGlobalePourcentage;

    @Column("remise_globale_montant")
    private Double remiseGlobaleMontant;

    @Column("apply_vat")
    private Boolean applyVat;
    
    // Currency & Payment
    @Column("devise")
    private String devise;

    @Column("taux_change")
    private Double tauxChange;

    @Column("mode_reglement")
    private TypePaiementFactureFournisseur modeReglement;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("nbre_echeance")
    private Integer nbreEcheance;
    
    // References
    @Column("nos_ref")
    private String nosRef;

    @Column("vos_ref")
    private String vosRef;

    @Column("reference_commande")
    private String referenceCommande;

    @Column("id_grn")
    private UUID idGRN;

    @Column("numero_grn")
    private String numeroGRN;
    
    @Column("lignes_facture")
    private List<LineFactureFournisseur> lignesFacture;
    
    @Column("notes")
    private String notes;

    @Column("created_by")
    private UUID createdBy;

    @Column("approved_by")
    private UUID approvedBy;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}