package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.ModeReglement;
import com.example.account.modules.facturation.model.enums.StatutProforma;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "factures_proforma",
    indexes = {
        @Index(name = "idx_proforma_org", columnList = "organization_id"),
        @Index(name = "idx_proforma_org_numero", columnList = "organization_id, numero_proforma_invoice")
    }
)
public class FactureProforma extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_proforma_invoice")
    private UUID idProformaInvoice;

    @NotBlank(message = "Le numéro de proforma est obligatoire")
    @Column(name = "numero_proforma_invoice", unique = true)
    private String numeroProformaInvoice;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "type")
    private String type;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutProforma statut;

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "Le client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "adresse_client")
    private String adresseClient;

    @Column(name = "email_client")
    private String emailClient;

    @Column(name = "telephone_client")
    private String telephoneClient;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lignes_facture_proforma", columnDefinition = "jsonb")
    @Builder.Default
    private List<LigneFactureProforma> lignesFactureProforma = new ArrayList<>();

    @Column(name = "montant_ht")
    private BigDecimal montantHT;

    @Column(name = "montant_tva")
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc")
    private BigDecimal montantTTC;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    private BigDecimal tauxChange;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "reference_externe")
    private String referenceExterne;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column(name = "date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column(name = "date_acceptation")
    private LocalDateTime dateAcceptation;

    @Column(name = "date_refus")
    private LocalDateTime dateRefus;

    @Column(name = "motif_refus")
    private String motifRefus;

    @Column(name = "id_facture_convertie")
    private UUID idFactureConvertie;

    @Column(name = "remise_globale_pourcentage")
    private BigDecimal remiseGlobalePourcentage;

    @Column(name = "remise_globale_montant")
    private BigDecimal remiseGlobaleMontant;

    @Column(name = "validite_offre_jours")
    private Integer validiteOffreJours;

    @Column(name = "apply_vat")
    @Builder.Default
    private Boolean applyVat = true;

    @Column(name = "date_systeme")
    private LocalDate dateSysteme;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_reglement")
    private ModeReglement modeReglement;

    @Column(name = "nos_ref")
    private String nosRef;

    @Column(name = "vos_ref")
    private String vosRef;

    @Column(name = "nbre_echeance")
    private Integer nbreEcheance;

    @Column(name = "referal_client_id")
    private UUID referalClientId;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
