package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "stock",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "id_produit", "emplacement"})
    },
    indexes = {
        @Index(name = "idx_stock_org", columnList = "organization_id"),
        @Index(name = "idx_stock_org_produit", columnList = "organization_id, id_produit")
    }
)
public class Stock extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_stock")
    private UUID idStock;

    @NotNull(message = "Le produit est obligatoire")
    @Column(name = "id_produit")
    private UUID idProduit;

    @Column(name = "nom_produit")
    private String nomProduit;

    @Column(name = "reference_produit")
    private String referenceProduit;

    @NotNull(message = "La quantité est obligatoire")
    @PositiveOrZero(message = "La quantité doit être positive ou nulle")
    @Column(name = "quantite")
    private BigDecimal quantite;

    @Column(name = "quantite_reservee")
    @Builder.Default
    private BigDecimal quantiteReservee = BigDecimal.ZERO;

    @Column(name = "quantite_disponible")
    private BigDecimal quantiteDisponible;

    @Column(name = "stock_minimum")
    @Builder.Default
    private BigDecimal stockMinimum = BigDecimal.ZERO;

    @Column(name = "stock_maximum")
    private BigDecimal stockMaximum;

    @Column(name = "unite_mesure")
    @Builder.Default
    private String uniteMesure = "UNITE";

    @Column(name = "emplacement")
    private String emplacement;

    @Column(name = "zone")
    private String zone;

    @Column(name = "allee")
    private String allee;

    @Column(name = "rayon")
    private String rayon;

    @Column(name = "valeur_stock")
    private BigDecimal valeurStock;

    @Column(name = "cout_moyen_unitaire")
    private BigDecimal coutMoyenUnitaire;

    @Column(name = "derniere_entree")
    private LocalDateTime derniereEntree;

    @Column(name = "derniere_sortie")
    private LocalDateTime derniereSortie;

    @Column(name = "date_dernier_inventaire")
    private LocalDateTime dateDernierInventaire;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "ACTIF";

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
