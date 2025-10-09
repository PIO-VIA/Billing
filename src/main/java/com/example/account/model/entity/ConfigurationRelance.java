package com.example.account.model.entity;

import com.example.account
.model.enums.TypeRelance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "configurations_relance")
public class ConfigurationRelance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_configuration")
    private UUID idConfiguration;

    @NotBlank(message = "Le nom de la configuration est obligatoire")
    @Column(name = "nom_configuration")
    private String nomConfiguration;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Le type de relance est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_relance")
    private TypeRelance typeRelance;

    @Column(name = "jours_avant_echeance")
    private Integer joursAvantEcheance;

    @Column(name = "jours_apres_echeance")
    private Integer joursApresEcheance;

    @Column(name = "montant_minimum")
    private BigDecimal montantMinimum;

    @Column(name = "montant_maximum")
    private BigDecimal montantMaximum;

    @Column(name = "template_email")
    private String templateEmail;

    @Column(name = "objet_email")
    private String objetEmail;

    @Column(name = "contenu_personnalise")
    private String contenuPersonnalise;

    @Column(name = "envoyer_par_email")
    @Builder.Default
    private Boolean envoyerParEmail = true;

    @Column(name = "envoyer_par_sms")
    @Builder.Default
    private Boolean envoyerParSms = false;

    @Column(name = "generer_pdf")
    @Builder.Default
    private Boolean genererPdf = false;

    @Column(name = "template_pdf")
    private String templatePdf;

    @Column(name = "inclure_facture_pdf")
    @Builder.Default
    private Boolean inclureFacturePdf = true;

    @Column(name = "copie_interne")
    private List<String> copieInterne;

    @Column(name = "heure_envoi")
    @Builder.Default
    private Integer heureEnvoi = 9; // 9h du matin

    @Column(name = "jours_semaine_envoi")
    private List<Integer> joursSemaineEnvoi; // 1-7 pour lundi-dimanche

    @Column(name = "exclure_weekends")
    @Builder.Default
    private Boolean exclureWeekends = true;

    @Column(name = "exclure_jours_feries")
    @Builder.Default
    private Boolean exclureJoursFeries = true;

    @Column(name = "delai_min_entre_relances")
    @Builder.Default
    private Integer delaiMinEntreRelances = 7; // jours

    @Column(name = "nombre_max_relances")
    @Builder.Default
    private Integer nombreMaxRelances = 3;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "ordre_priorite")
    @Builder.Default
    private Integer ordrePriorite = 1;

    @Column(name = "conditions_arret")
    private List<String> conditionsArret; // "PAIEMENT_RECU", "AVOIR_EMIS", etc.

    @Column(name = "escalade_automatique")
    @Builder.Default
    private Boolean escaladeAutomatique = false;

    @Column(name = "escalade_vers")
    private List<String> escaladeVers; // emails ou rôles

    @Column(name = "delai_escalade_jours")
    private Integer delaiEscaladeJours;

    @Column(name = "frais_relance")
    private BigDecimal fraisRelance;

    @Column(name = "appliquer_frais_automatiquement")
    @Builder.Default
    private Boolean appliquerFraisAutomatiquement = false;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}