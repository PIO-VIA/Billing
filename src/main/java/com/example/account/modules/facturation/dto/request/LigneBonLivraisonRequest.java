package com.example.account.modules.facturation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonAlias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneBonLivraisonRequest {

 

    @NotNull(message = "L'ID produit est obligatoire")
    @JsonAlias({"productId"})
    private UUID idProduit;

    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @JsonAlias({"quantity","quantite","qty"})
    private Integer quantite;

    @JsonAlias({"unitPrice","prixUnitaire","unit_price","price"})
    private BigDecimal prixUnitaire;
    @JsonAlias({"amount","montant","total","montant_total"})
    private BigDecimal montant;
}
