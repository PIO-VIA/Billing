package com.example.account.modules.facturation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import com.fasterxml.jackson.annotation.JsonAlias;
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
public class LigneDevisCreateRequest {

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @JsonAlias({"qty", "quantity"})
    private Integer quantite;

    @JsonAlias({"desc"})
    private String description;

    @PositiveOrZero(message = "Le débit doit être positif ou nul")
    @JsonAlias({"prixUnitaire", "unitPrice", "price"})
    private BigDecimal debit;

    @PositiveOrZero(message = "Le crédit doit être positif ou nul")
    @JsonAlias({"montantTotal", "totalAmount", "amount"})
    private BigDecimal credit;

    @Builder.Default
    private Boolean isTaxLine = false;

    @JsonAlias({"productId", "id_produit"})
    private UUID idProduit;

    @JsonAlias({"productName", "nom_produit"})
    private String nomProduit;

    @PositiveOrZero(message = "Le prix unitaire doit être positif ou nul")
    private BigDecimal prixUnitaire;

    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    private BigDecimal montantTotal;

    @PositiveOrZero(message = "La remise en pourcentage doit être positive ou nulle")
    private BigDecimal remisePourcentage;

    @PositiveOrZero(message = "La remise en montant doit être positive ou nulle")
    private BigDecimal remiseMontant;
}