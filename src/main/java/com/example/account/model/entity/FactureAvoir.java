package com.example.account.model.entity;

import com.example.account.model.enums.StatutAvoir;
import com.example.account.model.enums.TypeAvoir;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "factures_avoir",
    indexes = {
        @Index(name = "idx_factureavoir_org", columnList = "organization_id"),
        @Index(name = "idx_factureavoir_org_numeroavoir", columnList = "organization_id, numero_avoir")
    }
)
public class FactureAvoir extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_avoir")
    private UUID idAvoir;

    @NotBlank(message = "Le numéro d'avoir est obligatoire")
    @Column(name = "numero_avoir")
    private String numeroAvoir;

    @NotNull(message = "La date de création est obligatoire")
    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "date_validation")
    private LocalDate dateValidation;

    @NotNull(message = "Le type d'avoir est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_avoir")
    private TypeAvoir typeAvoir;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutAvoir statut;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "L'ID de la facture d'origine est obligatoire")
    @Column(name = "id_facture_origine")
    private UUID idFactureOrigine;

    @Column(name = "numero_facture_origine")
    private String numeroFactureOrigine;

    @NotNull(message = "L'ID client est obligatoire")
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

    @OneToMany(mappedBy = "factureAvoir", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneAvoir> lignesAvoir;

    @Column(name = "montant_ht")
    private BigDecimal montantHT;

    @Column(name = "montant_tva")
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc")
    private BigDecimal montantTTC;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column(name = "motif_avoir")
    private String motifAvoir;

    @Column(name = "notes")
    private String notes;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column(name = "date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column(name = "date_application")
    private LocalDateTime dateApplication;

    @Column(name = "montant_applique")
    @Builder.Default
    private BigDecimal montantApplique = BigDecimal.ZERO;

    @Column(name = "montant_rembourse")
    @Builder.Default
    private BigDecimal montantRembourse = BigDecimal.ZERO;

    @Column(name = "mode_remboursement")
    private String modeRemboursement;

    @Column(name = "reference_remboursement")
    private String referenceRemboursement;

    @Column(name = "date_remboursement")
    private LocalDateTime dateRemboursement;

    @Column(name = "approuve_par")
    private UUID approuvePar;

    @Column(name = "date_approbation")
    private LocalDateTime dateApprobation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}