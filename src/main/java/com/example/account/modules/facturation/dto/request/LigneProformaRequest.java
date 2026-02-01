package com.example.account.modules.facturation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class LigneProformaRequest {

    @NotNull(message = "L'ID produit est obligatoire")
    private UUID idProduit;

    private String nomProduit;
    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;

    private BigDecimal prixUnitaire;
    private BigDecimal remisePourcentage;
    private BigDecimal remiseMontant;
    private BigDecimal tauxTva;
}
