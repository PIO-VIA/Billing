package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.ModeReglementNoteCredit;
import com.example.account.modules.facturation.model.enums.StatutNoteCredit;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("note_credits")
public class NoteCredit extends OrganizationScoped {

    @Id
    @Column("id_note_credit")
    private UUID idNoteCredit;

    @Column("numero_note_credit")
    private String numeroNoteCredit;

    @Column("numero_facture")
    private String numeroFacture;

    @Column("date_facturation")
    private LocalDateTime dateFacturation;

    @Column("date_echeance")
    private LocalDateTime dateEcheance;

    @Column("date_systeme")
    private LocalDateTime dateSysteme;

    @Column("etat")
    private StatutNoteCredit etat;

    @Column("type")
    @Builder.Default
    private String type = "AVOIR";

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

    @Column("montant_total")
    private BigDecimal montantTotal;

    @Column("montant_restant")
    private BigDecimal montantRestant;

    @Column("final_amount")
    private BigDecimal finalAmount;

    @Column("remise_globale_pourcentage")
    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Column("remise_globale_montant")
    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Column("apply_vat")
    @Builder.Default
    private Boolean applyVat = true;

    @Column("devise")
    private String devise;

    @Column("taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column("mode_reglement")
    private ModeReglementNoteCredit modeReglement;

    @Column("conditions_paiement")
    private String conditionsPaiement;

    @Column("nbre_echeance")
    private Integer nbreEcheance;

    @Column("nos_ref")
    private String nosRef;

    @Column("vos_ref")
    private String vosRef;

    @Column("reference_commande")
    private String referenceCommande;

    @Column("id_devis_origine")
    private UUID idDevisOrigine;

    @Column("lignes_facture")
    private String lignesFactureJson;

    @Transient
    private List<LigneNoteCredit> lignesFacture;

    @Column("notes")
    private String notes;

    @Column("pdf_path")
    private String pdfPath;

    @Column("envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column("date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("referal_client_id")
    private String referalClientId;

    @Version
    @Column("version")
    @Builder.Default
    private Long version = 0L;
}
