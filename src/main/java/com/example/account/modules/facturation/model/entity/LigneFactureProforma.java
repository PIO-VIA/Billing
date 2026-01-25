package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "lignes_facture_proforma",
    indexes = {
        @Index(name = "idx_ligne_proforma_org", columnList = "organization_id"),
        @Index(name = "idx_ligne_proforma_head", columnList = "id_proforma_invoice")
    }
)
public class LigneFactureProforma extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ligne_proforma")
    private UUID idLigneProforma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proforma_invoice", nullable = false)
    private FactureProforma factureProforma;

    @NotNull(message = "L'ID produit est obligatoire")
    @Column(name = "id_produit")
    private UUID idProduit;

    @Column(name = "nom_produit")
    private String nomProduit;

    @Column(name = "description")
    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column(name = "quantite")
    private Integer quantite;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "remise_pourcentage")
    @Builder.Default
    private BigDecimal remisePourcentage = BigDecimal.ZERO;

    @Column(name = "remise_montant")
    @Builder.Default
    private BigDecimal remiseMontant = BigDecimal.ZERO;

    @Column(name = "taux_tva")
    private BigDecimal tauxTva;
}
