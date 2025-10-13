package com.example.account.model.entity;

import com.example.account
.model.enums.FrequenceRecurrence;
import com.example.account
.model.enums.StatutAbonnement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "abonnements_facturation")
public class AbonnementFacturation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_abonnement")
    private UUID idAbonnement;

    @NotBlank(message = "Le nom de l'abonnement est obligatoire")
    @Column(name = "nom_abonnement")
    private String nomAbonnement;

    @Column(name = "description")
    private String description;

    @NotNull(message = "L'ID client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "email_client")
    private String emailClient;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutAbonnement statut;

    @NotNull(message = "La fréquence de récurrence est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "frequence_recurrence")
    private FrequenceRecurrence frequenceRecurrence;

    @Column(name = "jour_facturation")
    private Integer jourFacturation; // jour du mois (1-31) ou jour de la semaine (1-7) selon fréquence

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "date_prochaine_facturation")
    private LocalDate dateProchaineFacturation;

    @Column(name = "date_derniere_facturation")
    private LocalDate dateDerniereFacturation;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou nul")
    @Column(name = "montant_recurrent")
    private BigDecimal montantRecurrent;

    // Stocker les IDs des lignes templates (peut être sérialisé en JSON dans votre service)
    @Column(name = "lignes_template", columnDefinition = "TEXT")
    private String lignesTemplateJson;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "notes_template")
    private String notesTemplate;

    @Column(name = "nombre_factures_generees")
    @Builder.Default
    private Integer nombreFacturesGenerees = 0;

    @Column(name = "nombre_max_factures")
    private Integer nombreMaxFactures; // null = illimité

    @Column(name = "montant_total_facture")
    @Builder.Default
    private BigDecimal montantTotalFacture = BigDecimal.ZERO;

    @Column(name = "auto_envoyer_email")
    @Builder.Default
    private Boolean autoEnvoyerEmail = false;

    @Column(name = "auto_generer_pdf")
    @Builder.Default
    private Boolean autoGenererPdf = true;

    @Column(name = "jours_avant_rappel")
    @Builder.Default
    private Integer joursAvantRappel = 3;

    @Column(name = "template_email_personnalise")
    private String templateEmailPersonnalise;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "derniere_erreur")
    private String derniereErreur;

    @Column(name = "date_derniere_erreur")
    private LocalDateTime dateDerniereErreur;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}