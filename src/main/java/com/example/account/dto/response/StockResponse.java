package com.example.account.dto.response;

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
public class StockResponse {
    private UUID idStock;
    private UUID idProduit;
    private String nomProduit;
    private String referenceProduit;
    private BigDecimal quantite;
    private BigDecimal quantiteReservee;
    private BigDecimal quantiteDisponible;
    private BigDecimal stockMinimum;
    private BigDecimal stockMaximum;
    private String uniteMesure;
    private String emplacement;
    private String zone;
    private String allee;
    private String rayon;
    private BigDecimal valeurStock;
    private BigDecimal coutMoyenUnitaire;
    private LocalDateTime derniereEntree;
    private LocalDateTime derniereSortie;
    private LocalDateTime dateDernierInventaire;
    private String statut;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean alerteStockBas;
}
