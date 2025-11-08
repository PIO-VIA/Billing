package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "plan_tresorerie", indexes = {
    @Index(name = "idx_tresorerie_periode", columnList = "annee, mois")
})
public class PlanTresorerie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tresorerie")
    private UUID idTresorerie;

    @NotNull(message = "L'année est obligatoire")
    @Column(name = "annee")
    private Integer annee;

    @NotNull(message = "Le mois est obligatoire")
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    @Column(name = "mois")
    private Integer mois;

    @Column(name = "periode")
    private String periode; // Format: YYYY-MM

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @NotNull(message = "Le solde initial est obligatoire")
    @Column(name = "solde_initial")
    @Builder.Default
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(name = "encaissements_prevus")
    @Builder.Default
    private BigDecimal encaissementsPrevus = BigDecimal.ZERO;

    @Column(name = "encaissements_clients")
    @Builder.Default
    private BigDecimal encaissementsClients = BigDecimal.ZERO;

    @Column(name = "autres_encaissements")
    @Builder.Default
    private BigDecimal autresEncaissements = BigDecimal.ZERO;

    @Column(name = "decaissements_prevus")
    @Builder.Default
    private BigDecimal decaissementsPrevus = BigDecimal.ZERO;

    @Column(name = "decaissements_fournisseurs")
    @Builder.Default
    private BigDecimal decaissementsFournisseurs = BigDecimal.ZERO;

    @Column(name = "salaires")
    @Builder.Default
    private BigDecimal salaires = BigDecimal.ZERO;

    @Column(name = "charges_sociales")
    @Builder.Default
    private BigDecimal chargesSociales = BigDecimal.ZERO;

    @Column(name = "impots_taxes")
    @Builder.Default
    private BigDecimal impotsTaxes = BigDecimal.ZERO;

    @Column(name = "autres_decaissements")
    @Builder.Default
    private BigDecimal autresDecaissements = BigDecimal.ZERO;

    @Column(name = "solde_final_prevu")
    private BigDecimal soldeFinalPrevu;

    @Column(name = "encaissements_reels")
    @Builder.Default
    private BigDecimal encaissementsReels = BigDecimal.ZERO;

    @Column(name = "decaissements_reels")
    @Builder.Default
    private BigDecimal decaissementsReels = BigDecimal.ZERO;

    @Column(name = "solde_final_reel")
    private BigDecimal soldeFinalReel;

    @Column(name = "ecart")
    private BigDecimal ecart;

    @Column(name = "devise")
    @Builder.Default
    private String devise = "EUR";

    @Column(name = "statut")
    @Builder.Default
    private String statut = "PREVISIONNEL";

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cloture")
    @Builder.Default
    private Boolean cloture = false;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;
}
