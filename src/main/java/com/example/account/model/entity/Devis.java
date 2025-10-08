package com.example.account.model.entity;

import com.yooyob.erp.model.enums.StatutDevis;
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
@Table(name = "devis")
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_devis")
    private UUID idDevis;

    @NotBlank(message = "Le numéro de devis est obligatoire")
    @Column(name = "numero_devis")
    private String numeroDevis;

    @NotNull(message = "La date de création est obligatoire")
    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @NotNull(message = "La date de validité est obligatoire")
    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @Column(name = "type")
    private String type;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutDevis statut;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column(name = "montant_total")
    private BigDecimal montantTotal;

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

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneDevis> lignesDevis;

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

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "notes")
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
    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Column(name = "remise_globale_montant")
    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Column(name = "validite_offre_jours")
    @Builder.Default
    private Integer validiteOffreJours = 30;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}