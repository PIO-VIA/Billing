package com.example.account.model.entity;

import com.yooyob.erp.model.enums.TypeNumerotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "configurations_numerotation")
public class ConfigurationNumerotation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_configuration")
    private UUID idConfiguration;

    @NotNull(message = "Le type de numérotation est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_numerotation")
    private TypeNumerotation typeNumerotation;

    @NotBlank(message = "Le nom de la configuration est obligatoire")
    @Column(name = "nom_configuration")
    private String nomConfiguration;

    @Column(name = "description")
    private String description;

    @NotBlank(message = "Le modèle de numérotation est obligatoire")
    @Column(name = "modele_numerotation")
    private String modeleNumerotation; // ex: "{PREFIX}-{YYYY}-{MM}-{SEQUENCE:5}"

    @Column(name = "prefixe")
    private String prefixe;

    @Column(name = "suffixe")
    private String suffixe;

    @Column(name = "separateur")
    @Builder.Default
    private String separateur = "-";

    @Column(name = "longueur_sequence")
    @Builder.Default
    private Integer longueurSequence = 5;

    @Column(name = "valeur_initiale")
    @Builder.Default
    private Long valeurInitiale = 1L;

    @Column(name = "increment")
    @Builder.Default
    private Long increment = 1L;

    @Column(name = "reset_periodique")
    @Builder.Default
    private Boolean resetPeriodique = false;

    @Column(name = "periode_reset")
    private String periodeReset; // "ANNEE", "MOIS", "JOUR"

    @Column(name = "inclure_annee")
    @Builder.Default
    private Boolean inclureAnnee = true;

    @Column(name = "format_annee")
    @Builder.Default
    private String formatAnnee = "yyyy"; // "yyyy", "yy"

    @Column(name = "inclure_mois")
    @Builder.Default
    private Boolean inclureMois = false;

    @Column(name = "format_mois")
    @Builder.Default
    private String formatMois = "MM"; // "MM", "M"

    @Column(name = "inclure_jour")
    @Builder.Default
    private Boolean inclureJour = false;

    @Column(name = "format_jour")
    @Builder.Default
    private String formatJour = "dd"; // "dd", "d"

    @Column(name = "variables_personnalisees")
    private Map<String, String> variablesPersonnalisees;

    @Column(name = "conditions_application")
    private String conditionsApplication; // expressions pour définir quand appliquer cette config

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "par_defaut")
    @Builder.Default
    private Boolean parDefaut = false;

    @Column(name = "priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column(name = "site")
    private String site; // pour multi-sites

    @Column(name = "departement")
    private String departement;

    @Column(name = "utilisateur_specifique")
    private UUID utilisateurSpecifique;

    @Column(name = "date_debut_validite")
    private LocalDateTime dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDateTime dateFinValidite;

    @Column(name = "test_mode")
    @Builder.Default
    private Boolean testMode = false;

    @Column(name = "exemples_generes")
    private String exemplesGeneres;

    @Column(name = "derniere_sequence_utilisee")
    @Builder.Default
    private Long derniereSequenceUtilisee = 0L;

    @Column(name = "derniere_utilisation")
    private LocalDateTime derniereUtilisation;

    @Column(name = "compteur_utilisation")
    @Builder.Default
    private Long compteurUtilisation = 0L;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}