package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.ModeReglement;
import com.example.account.modules.facturation.model.enums.StatutProforma;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@EqualsAndHashCode(callSuper = true)
@Table("factures_proforma")
public class FactureProforma extends OrganizationScoped {

    @Id
    @Column("id_proforma_invoice")
    private UUID idProformaInvoice;

    @Column("numero_proforma_invoice")
    private String numeroProformaInvoice;

    @Column("date_creation")
    private LocalDateTime dateCreation;

    @Column("type")
    private String type;

    @Column("statut")
    private StatutProforma statut;

    @Column("montant_total")
    private BigDecimal montantTotal;

    @Column("id_client")
    private UUID idClient;

    @Column("nom_client")
    private String nomClient;

    @Column("adresse_client")
    private String adresseClient;

    @Column("email_client")
    private String emailClient;

    @Column("telephone_client")
    private String telephoneClient;

    @Transient
    @Builder.Default
    private List<LigneFactureProforma> lignesFactureProforma = new ArrayList<>();

    @Column("montant_ht")
    private BigDecimal montantHT;

    @Column("montant_tva")
    private BigDecimal montantTVA;

    @Column("montant_ttc")
    private BigDecimal montantTTC;

    @Column("devise")
    private String devise;

    @Column("taux_change")
    private BigDecimal tauxChange;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("notes")
    private String notes;

    @Column("reference_externe")
    private String referenceExterne;

    @Column("pdf_path")
    private String pdfPath;

    @Column("envoye_par_email")
    private Boolean envoyeParEmail;

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
    private BigDecimal remiseGlobalePourcentage;

    @Column("remise_globale_montant")
    private BigDecimal remiseGlobaleMontant;

    @Column("validite_offre_jours")
    private Integer validiteOffreJours;

    @Column("apply_vat")
    private Boolean applyVat;

    @Column("date_systeme")
    private LocalDate dateSysteme;

    @Column("mode_reglement")
    private ModeReglement modeReglement;

    @Column("nos_ref")
    private String nosRef;

    @Column("vos_ref")
    private String vosRef;

    @Column("nbre_echeance")
    private Integer nbreEcheance;

    @Column("referal_client_id")
    private UUID referalClientId;

    @Column("final_amount")
    private BigDecimal finalAmount;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
