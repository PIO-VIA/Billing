package com.yooyob.erp.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ligne_facture")
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ligne")
    private UUID idLigne;

    @NotNull(message = "L'ID de la facture est obligatoire")
    @Column(name = "id_facture")
    private UUID idFacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture", insertable = false, updatable = false)
    private Facture facture;

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

    @Column(name = "reference_produit")
    private String referenceProduit;

    @Column(name = "nom_produit")
    private String nomProduit;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "ordre")
    private Integer ordre;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
