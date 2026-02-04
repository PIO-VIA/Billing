package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("lignes_bon_livraison")
public class LigneBonLivraison extends OrganizationScoped {

    @Id
    @Column("id_ligne_bon_livraison")
    private UUID idLigneBonLivraison;

    @Column("id_bon_livraison")
    private UUID idBonLivraison;

    @Column("id_produit")
    private UUID idProduit;

    @Column("description")
    private String description;

    @Column("quantite")
    private Integer quantite;

    @Column("prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column("montant")
    private BigDecimal montant;
}
