package com.example.account.model.entity;

import com.example.account
.model.enums.TypeEscompte;
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
@Table(name = "escomptes_paiement")
public class EscomptePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_escompte")
    private UUID idEscompte;

    @NotBlank(message = "Le nom de l'escompte est obligatoire")
    @Column(name = "nom_escompte")
    private String nomEscompte;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Le type d'escompte est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_escompte")
    private TypeEscompte typeEscompte;

    @Column(name = "code_escompte")
    private String codeEscompte;

    @PositiveOrZero(message = "Le pourcentage doit être positif ou nul")
    @Column(name = "pourcentage_escompte")
    private BigDecimal pourcentageEscompte;

    @PositiveOrZero(message = "Le montant fixe doit être positif ou nul")
    @Column(name = "montant_fixe")
    private BigDecimal montantFixe;

    @Column(name = "montant_minimum")
    private BigDecimal montantMinimum;

    @Column(name = "montant_maximum")
    private BigDecimal montantMaximum;

    @Column(name = "date_debut_validite")
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;

    @Column(name = "jours_paiement_anticipe")
    private Integer joursPaiementAnticipe;

    @Column(name = "jours_maximum_anticipe")
    private Integer joursMaximumAnticipe;

    @Column(name = "cumul_possible")
    @Builder.Default
    private Boolean cumulPossible = false;

    @Column(name = "escomptes_exclus")
    private List<UUID> escomptesExclus;

    @Column(name = "clients_eligibles")
    private List<UUID> clientsEligibles;

    @Column(name = "types_factures")
    private List<String> typesFactures;

    @Column(name = "produits_eligibles")
    private List<UUID> produitsEligibles;

    @Column(name = "categories_eligibles")
    private List<String> categoriesEligibles;

    @Column(name = "montant_commande_minimum")
    private BigDecimal montantCommandeMinimum;

    @Column(name = "quantite_minimum")
    private Integer quantiteMinimum;

    @Column(name = "premiere_commande_uniquement")
    @Builder.Default
    private Boolean premiereCommandeUniquement = false;

    @Column(name = "frequence_utilisation_max")
    private Integer frequenceUtilisationMax;

    @Column(name = "nombre_utilisations")
    @Builder.Default
    private Integer nombreUtilisations = 0;

    @Column(name = "nombre_max_utilisations")
    private Integer nombreMaxUtilisations;

    @Column(name = "budget_total_escompte")
    private BigDecimal budgetTotalEscompte;

    @Column(name = "budget_utilise")
    @Builder.Default
    private BigDecimal budgetUtilise = BigDecimal.ZERO;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "automatique")
    @Builder.Default
    private Boolean automatique = false;

    @Column(name = "code_promo_requis")
    @Builder.Default
    private Boolean codePromoRequis = false;

    @Column(name = "validation_manuelle")
    @Builder.Default
    private Boolean validationManuelle = false;

    @Column(name = "conditions_particulieres")
    private String conditionsParticulieres;

    @Column(name = "message_client")
    private String messageClient;

    @Column(name = "priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column(name = "groupe_escomptes")
    private String groupeEscomptes;

    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "statistiques_utilisation")
    private String statistiquesUtilisation;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}