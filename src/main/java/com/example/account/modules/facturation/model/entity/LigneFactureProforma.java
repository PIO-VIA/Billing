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
@Table("lignes_facture_proforma")
public class LigneFactureProforma extends OrganizationScoped {

    @Id
    @Column("id_ligne_proforma")
    private UUID idLigneProforma;

    @Column("id_proforma_invoice")
    private UUID idProformaInvoice;

    @Column("id_produit")
    private UUID idProduit;

    @Column("nom_produit")
    private String nomProduit;

    @Column("description")
    private String description;

    @Column("quantite")
    private Integer quantite;

    @Column("prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column("montant_total")
    private BigDecimal montantTotal;

    @Column("remise_pourcentage")
    private BigDecimal remisePourcentage;

    @Column("remise_montant")
    private BigDecimal remiseMontant;

    @Column("taux_tva")
    private BigDecimal tauxTva;
}
