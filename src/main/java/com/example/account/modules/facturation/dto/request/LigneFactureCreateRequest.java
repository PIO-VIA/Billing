package com.example.account.modules.facturation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneFactureCreateRequest {

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;

    private String description;

    private BigDecimal debit;

    private BigDecimal credit;

    @Builder.Default
    private Boolean isTaxLine = false;

    private UUID idProduit;

    private String nomProduit;

    private BigDecimal prixUnitaire;

    private BigDecimal montantTotal;
}