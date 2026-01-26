package com.example.account.modules.facturation.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.example.account.modules.facturation.model.entity.Lines.LineFactureFournisseur;
import com.example.account.modules.facturation.model.enums.StatutFactureFournisseur;
import com.example.account.modules.facturation.model.enums.TypePaiementFactureFournisseur;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString @Builder
@Entity
@Table(name = "factures_fournisseurs")
public class FactureFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idFacture;
    private String numeroFacture;
    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
    
    @CreationTimestamp
    private LocalDateTime dateSysteme;

    @Enumerated(EnumType.STRING)
    private StatutFactureFournisseur etat;
    private String type;
    
    // Supplier Info
    private UUID idFournisseur;
    private String nomFournisseru; 
    private String adresseFournisseur;
    private String emailFournisseur;
    private String telephoneFournisseur;
    
    // Financials
    private Double montantHT;
    private Double montantTVA;
    private Double montantTTC;
    private Double montantTotal;
    private Double montantRestant;
    private Double finalAmount;
    
    // Discounts & VAT
    private Double remiseGlobalePourcentage;
    private Double remiseGlobaleMontant;
    private Boolean applyVat;
    
    // Currency & Payment
    private String devise;
    private Double tauxChange;

    @Enumerated(EnumType.STRING)
    private TypePaiementFactureFournisseur modeReglement;
    private String conditionsPaiement;
    private Integer nbreEcheance;
    
    // References
    private String nosRef;
    private String vosRef;
    private String referenceCommande;
    private UUID idGRN;
    private String numeroGRN;
    
    /**
     * Set the lines as JSONB in the database
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<LineFactureFournisseur> lignesFacture;
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    private UUID createdBy;
    private UUID approvedBy;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}