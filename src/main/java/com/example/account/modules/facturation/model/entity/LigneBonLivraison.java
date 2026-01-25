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
    name = "lignes_bon_livraison",
    indexes = {
        @Index(name = "idx_lignebonlivraison_org", columnList = "organization_id"),
        @Index(name = "idx_lignebonlivraison_bonlivraison", columnList = "id_bon_livraison")
    }
)
public class LigneBonLivraison extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ligne_bon_livraison")
    private UUID idLigneBonLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bon_livraison", nullable = false)
    private BonLivraison bonLivraison;

    @NotNull(message = "L'ID produit est obligatoire")
    @Column(name = "id_produit")
    private UUID idProduit;

    @Column(name = "description")
    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column(name = "quantite")
    private Integer quantite;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column(name = "montant")
    private BigDecimal montant;
}
