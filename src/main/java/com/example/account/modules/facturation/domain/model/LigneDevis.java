package com.example.account.modules.facturation.domain.model;

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
public class LigneDevis {
    private UUID idLigne;
    private Double quantite;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private Boolean isTaxLine;
    private String idProduit;
    private String nomProduit;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
    private BigDecimal remisePourcentage;
    private BigDecimal remiseMontant;
}
