package com.example.account.modules.facturation.model.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneBonLivraison  {

    @NotNull(message = "L'ID produit est obligatoire")
    @Column("id_produit")
    private UUID idProduit;

    @Column("description")
    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column("quantite")
    private Integer quantite;

    @Column("prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column("montant")
    private BigDecimal montant;
}
