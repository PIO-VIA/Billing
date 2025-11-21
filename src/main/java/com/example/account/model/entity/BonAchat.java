package com.example.account.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bons_achat")
public class BonAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bon_achat")
    private UUID idBonAchat;

    @NotBlank(message = "Le numéro du bon est obligatoire")
    @Column(name = "numero_bon", unique = true)
    private String numeroBon;

    @NotNull(message = "La date d'achat est obligatoire")
    @Column(name = "date_achat")
    private LocalDate dateAchat;

    @Column(name = "date_livraison")
    private LocalDate dateLivraison;

    @NotNull(message = "Le fournisseur est obligatoire")
    @Column(name = "id_fournisseur")
    private UUID idFournisseur;

    @Column(name = "nom_fournisseur")
    private String nomFournisseur;

    @Column(name = "id_bon_commande")
    private UUID idBonCommande;

    @Column(name = "numero_commande_ref")
    private String numeroCommandeRef;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "montant_ht")
    private BigDecimal montantHT;

    @Column(name = "montant_tva")
    private BigDecimal montantTVA;

    @Builder.Default
    @Column(name = "devise")
    private String devise = "EUR";

    @Builder.Default
    @Column(name = "taux_change")
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Builder.Default
    @Column(name = "statut")
    private String statut = "EN_ATTENTE";

    @Column(name = "numero_facture_fournisseur")
    private String numeroFactureFournisseur;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

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
