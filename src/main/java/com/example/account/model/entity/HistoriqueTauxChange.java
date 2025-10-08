package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "historique_taux_change")
public class HistoriqueTauxChange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historique")
    private UUID idHistorique;

    @NotBlank(message = "La devise source est obligatoire")
    @Column(name = "devise_source")
    private String deviseSource;

    @NotBlank(message = "La devise cible est obligatoire")
    @Column(name = "devise_cible")
    private String deviseCible;

    @NotNull(message = "Le taux de change est obligatoire")
    @Positive(message = "Le taux de change doit être positif")
    @Column(name = "taux_change")
    private BigDecimal tauxChange;

    @NotNull(message = "La date d'application est obligatoire")
    @Column(name = "date_application")
    private LocalDateTime dateApplication;

    @Column(name = "date_fin_validite")
    private LocalDateTime dateFinValidite;

    @Column(name = "source_taux")
    private String sourceTaux; // "BANQUE_CENTRALE", "API_EXTERNE", "MANUEL", etc.

    @Column(name = "taux_achat")
    private BigDecimal tauxAchat;

    @Column(name = "taux_vente")
    private BigDecimal tauxVente;

    @Column(name = "taux_moyen")
    private BigDecimal tauxMoyen;

    @Column(name = "spread")
    private BigDecimal spread;

    @Column(name = "commission_pourcentage")
    private BigDecimal commissionPourcentage;

    @Column(name = "commission_fixe")
    private BigDecimal commissionFixe;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "automatique")
    @Builder.Default
    private Boolean automatique = false;

    @Column(name = "reference_externe")
    private String referenceExterne;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}