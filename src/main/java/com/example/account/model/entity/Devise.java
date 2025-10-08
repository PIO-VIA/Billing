package com.example.account.model.entity;

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
@Table(name = "devises")
public class Devise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_devise")
    private UUID idDevise;

    @NotBlank(message = "Le nom de la devise est obligatoire")
    @Column(name = "nom_devise")
    private String nomDevise;

    @NotBlank(message = "Le symbole est obligatoire")
    @Column(name = "symbole")
    private String symbole;

    @NotBlank(message = "Le code ISO est obligatoire")
    @Column(name = "code_iso")
    private String codeIso;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "devise_base")
    @Builder.Default
    private Boolean deviseBase = false;

    @Column(name = "taux_change_actuel")
    @Builder.Default
    private BigDecimal tauxChangeActuel = BigDecimal.ONE;

    @Column(name = "derniere_mise_a_jour_taux")
    private LocalDateTime derniereMiseAJourTaux;

    @Column(name = "source_taux_auto")
    private String sourceTauxAuto;

    @Column(name = "precision_decimale")
    @Builder.Default
    private Integer precisionDecimale = 2;

    @Column(name = "position_symbole")
    @Builder.Default
    private String positionSymbole = "AVANT"; // AVANT, APRES

    @Column(name = "separateur_milliers")
    @Builder.Default
    private String separateurMilliers = " ";

    @Column(name = "separateur_decimal")
    @Builder.Default
    private String separateurDecimal = ",";

    @Column(name = "unite_monetaire")
    private String uniteMonetaire;

    @Column(name = "sous_unite_monetaire")
    private String sousUniteMonetaire;

    @NotNull(message = "Le facteur de conversion est obligatoire")
    @Positive(message = "Le facteur de conversion doit être positif")
    @Column(name = "facteur_conversion")
    private BigDecimal facteurConversion;

    @Column(name = "nom_mesure")
    private String nomMesure;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}