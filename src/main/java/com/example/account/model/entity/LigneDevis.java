package com.example.account.model.entity;

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
@Table(name = "ligne_devis")
public class LigneDevis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ligne")
    private UUID idLigne;

    @NotNull(message = "L'ID du devis est obligatoire")
    @Column(name = "id_devis")
    private UUID idDevis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devis", insertable = false, updatable = false)
    private Devis devis;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column(name = "quantite")
    private Integer quantite;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Le débit est obligatoire")
    @Column(name = "debit")
    private BigDecimal debit;

    @NotNull(message = "Le crédit est obligatoire")
    @Column(name = "credit")
    private BigDecimal credit;

    @Column(name = "is_tax_line")
    @Builder.Default
    private Boolean isTaxLine = false;

    @Column(name = "id_produit")
    private UUID idProduit;

    @Column(name = "nom_produit")
    private String nomProduit;

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
}