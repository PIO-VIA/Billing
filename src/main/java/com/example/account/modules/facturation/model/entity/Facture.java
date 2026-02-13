package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.dto.request.LigneFactureCreateRequest;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.facturation.model.enums.TypePaiementFacture;
import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "factures",
    indexes = {
        @Index(name = "idx_facture_org", columnList = "organization_id"),
        @Index(name = "idx_facture_org_numero", columnList = "organization_id, numero_facture"),
        @Index(name = "idx_facture_org_client", columnList = "organization_id, id_client")
    }
)
public class Facture extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_facture")
    private UUID idFacture;

    @NotBlank(message = "Le numéro de facture est obligatoire")
    @Column(name = "numero_facture")
    private String numeroFacture;

    @NotNull(message = "La date de facturation est obligatoire")
    @Column(name = "date_facturation")
    private LocalDateTime dateFacturation;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column(name = "date_echeance")
    private LocalDateTime dateEcheance;

    @Column(name = "date_systeme")
    private LocalDateTime dateSysteme;

    @Column(name = "type")
    private String type;

    @NotNull(message = "L'état est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "etat")
    private StatutFacture etat;

    @NotNull(message = "L'ID client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient; // String to support 'c001' style IDs

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "adresse_client")
    private String adresseClient;

    @Column(name = "email_client")
    private String emailClient;

    @Column(name = "telephone_client")
    private String telephoneClient;

    // --- JSON STORAGE FOR LINES ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lignes_facture", columnDefinition = "jsonb")
    private List<LigneFacture> lignesFacture;

    @Column(name = "montant_ht")
    private BigDecimal montantHT;

    @Column(name = "montant_tva")
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc")
    private BigDecimal montantTTC;

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "montant_restant")
    private BigDecimal montantRestant;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;

    @Column(name = "apply_vat")
    private Boolean applyVat;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_reglement")
    private TypePaiementFacture modeReglement;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "nbre_echeance")
    private Integer nbreEcheance;

    @Column(name = "nos_ref")
    private String nosRef;

    @Column(name = "vos_ref")
    private String vosRef;

    @Column(name = "reference_commande")
    private String referenceCommande;

    @Column(name = "id_devis_origine")
    private String idDevisOrigine;

    @Column(name = "referal_client_id")
    private UUID referalClientId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column(name = "date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column(name = "remise_globale_pourcentage")
    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Column(name = "remise_globale_montant")
    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "validated_by")
    private UUID validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    @Builder.Default
    private Long version = 0L;

    private UUID organizationId;
    
}