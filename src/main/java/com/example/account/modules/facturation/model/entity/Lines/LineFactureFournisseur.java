package com.example.account.modules.facturation.model.entity.Lines;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineFactureFournisseur {
     private UUID idLigne;
    private Double quantite;
    private String description;
    private Double debit;
    private Double credit;
    private Boolean isTaxLine;
    private UUID idProduit;
    private String nomProduit;
    private Double prixUnitaire;
    private Double montantTotal;
    private Double remisePourcentage;
    private Double remiseMontant;
}

