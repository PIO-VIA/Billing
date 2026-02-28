package com.example.account.modules.facturation.dto.response;

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
public class LigneBonLivraisonResponse {

    private UUID idProduit;
    private String description;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montant;
}
