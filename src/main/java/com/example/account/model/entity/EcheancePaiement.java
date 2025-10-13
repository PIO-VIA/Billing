package com.example.account.model.entity;

import com.example.account
.model.enums.TypeEcheance;
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
@Table(name = "echeances_paiement")
public class EcheancePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_echeance")
    private UUID idEcheance;

    @NotNull(message = "L'ID de la facture est obligatoire")
    @Column(name = "id_facture")
    private UUID idFacture;

    @Column(name = "numero_facture")
    private String numeroFacture;

    @NotNull(message = "Le type d'échéance est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_echeance")
    private TypeEcheance typeEcheance;

    @Column(name = "numero_echeance")
    private Integer numeroEcheance;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou nul")
    @Column(name = "montant_echeance")
    private BigDecimal montantEcheance;

    @Column(name = "pourcentage_facture")
    private BigDecimal pourcentageFacture;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "montant_paye")
    @Builder.Default
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(name = "montant_restant")
    private BigDecimal montantRestant;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "EN_ATTENTE"; // EN_ATTENTE, PAYE, PARTIEL, RETARD

    @Column(name = "jours_retard")
    @Builder.Default
    private Integer joursRetard = 0;

    @Column(name = "penalites_retard")
    @Builder.Default
    private BigDecimal penalitesRetard = BigDecimal.ZERO;

    @Column(name = "taux_penalite")
    private BigDecimal tauxPenalite;

    // Liste des IDs d'escomptes applicables à cette échéance
    @Column(name = "escomptes_applicables")
    private List<UUID> escomptesApplicables;

    @Column(name = "escompte_applique")
    private UUID escompteApplique;

    @Column(name = "montant_escompte")
    @Builder.Default
    private BigDecimal montantEscompte = BigDecimal.ZERO;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "mode_paiement_suggere")
    private String modePaiementSuggere;

    @Column(name = "reference_paiement")
    private String referencePaiement;

    @Column(name = "notes")
    private String notes;

    @Column(name = "rappels_envoyes")
    @Builder.Default
    private Integer rappelsEnvoyes = 0;

    @Column(name = "derniere_relance")
    private LocalDateTime derniereRelance;

    @Column(name = "prochaine_relance")
    private LocalDateTime prochaineRelance;

    @Column(name = "blocage_client")
    @Builder.Default
    private Boolean blocageClient = false;

    @Column(name = "date_blocage")
    private LocalDateTime dateBlocage;

    @Column(name = "motif_blocage")
    private String motifBlocage;

    @Column(name = "priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column(name = "montant_devise_origine")
    private BigDecimal montantDeviseOrigine;

    @Column(name = "conditions_personnalisees")
    private String conditionsPersonnalisees;

    @Column(name = "validation_requise")
    @Builder.Default
    private Boolean validationRequise = false;

    @Column(name = "valide_par")
    private UUID validePar;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}