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
@Table(
    name = "bons_commande",
    indexes = {
        @Index(name = "idx_boncommande_org", columnList = "organization_id"),
        @Index(name = "idx_boncommande_org_numerobon", columnList = "organization_id, numero_bon")
    }
)
public class BonCommande extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bon_commande")
    private UUID idBonCommande;

    @NotBlank(message = "Le numéro de commande est obligatoire")
    @Column(name = "numero_commande", unique = true)
    private String numeroCommande;

    @NotNull(message = "La date de commande est obligatoire")
    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @Column(name = "date_livraison_prevue")
    private LocalDate dateLivraisonPrevue;

    @NotNull(message = "Le fournisseur est obligatoire")
    @Column(name = "id_fournisseur")
    private UUID idFournisseur;

    @Column(name = "nom_fournisseur")
    private String nomFournisseur;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "montant_ht")
    private BigDecimal montantHT;

    @Column(name = "montant_tva")
    private BigDecimal montantTVA;

    @Column(name = "devise")
    @Builder.Default
    private String devise = "EUR";

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "BROUILLON";

    @Column(name = "reference_externe")
    private String referenceExterne;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "delai_livraison")
    private Integer delaiLivraison;

    @Column(name = "adresse_livraison", length = 500)
    private String adresseLivraison;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validated_by")
    private String validatedBy;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.statut == null) {
            this.statut = "EN_ATTENTE";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
