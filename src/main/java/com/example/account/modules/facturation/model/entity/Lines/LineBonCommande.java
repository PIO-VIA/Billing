package com.example.account.modules.facturation.model.entity.Lines;

import java.math.BigDecimal;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineBonCommande {
    
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
