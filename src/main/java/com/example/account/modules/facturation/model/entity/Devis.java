package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.model.enums.TypePaiementDevis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("devis")
public class Devis extends OrganizationScoped {

    @Id
    @Column("id_devis")
    private UUID idDevis;

    @NotBlank(message = "Le numéro de devis est obligatoire")
    @Column("numero_devis")
    private String numeroDevis;

    @NotNull(message = "La date de création est obligatoire")
    @Column("date_creation")
    private LocalDateTime dateCreation;

    @NotNull(message = "La date de validité est obligatoire")
    @Column("date_validite")
    private LocalDateTime dateValidite;

    @Column("type")
    private String type;

    @NotNull(message = "Le statut est obligatoire")
    @Column("statut")
    private StatutDevis statut;

    @Column("lignes_devis")
    private List<LigneDevis> lignesDevis; 

    @NotNull(message = "Le montant total est obligatoire")
    @Column("montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "L'ID client est obligatoire")
    @Column("id_client")
    private String idClient;

    @Column("nom_client")
    private String nomClient;
    @Column("adresse_client")
    private String adresseClient;
    @Column("email_client")
    private String emailClient;
    @Column("telephone_client")
    private String telephoneClient;

    @Column("montant_ht")
    private BigDecimal montantHT;
    @Column("montant_tva")
    private BigDecimal montantTVA;
    @Column("montant_ttc")
    private BigDecimal montantTTC;
    @Column("devise")
    private String devise;

    @Column("taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("notes")
    private String notes;

    @Column("reference_externe")
    private String referenceExterne;
    @Column("pdf_path")
    private String pdfPath;

    @Column("envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column("date_envoi_email")
    private LocalDateTime dateEnvoiEmail;
    @Column("date_acceptation")
    private LocalDateTime dateAcceptation;
    @Column("date_refus")
    private LocalDateTime dateRefus;
    @Column("motif_refus")
    private String motifRefus;

    @Column("id_facture_convertie")
    private UUID idFactureConvertie;

    @Column("remise_globale_pourcentage")
    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Column("remise_globale_montant")
    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Column("validite_offre_jours")
    @Builder.Default
    private Integer validiteOffreJours = 30;

    @Column("apply_vat")
    @Builder.Default
    private Boolean applyVat = true;

    @Column("date_systeme")
    private LocalDateTime dateSysteme;

    @Column("mode_reglement")
    private TypePaiementDevis modeReglement;

    @Column("nos_ref")
    private String nosRef;
    @Column("vos_ref")
    private String vosRef;
    @Column("nbre_echeance")
    private Integer nbreEcheance;
    @Column("referal_client_id")
    private String referalClientId;
    @Column("final_amount")
    private BigDecimal finalAmount;

    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("version")
    @Builder.Default
    private Long version = 0L;
}