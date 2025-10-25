package com.example.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableauDeBordResponse {

    // Indicateurs financiers
    private BigDecimal chiffreAffairesMois;
    private BigDecimal chiffreAffairesAnnee;
    private BigDecimal evolutionCA;

    // Factures
    private Long nombreFacturesEmises;
    private Long nombreFacturesPayees;
    private Long nombreFacturesEnAttente;
    private BigDecimal montantFacturesEnAttente;
    private BigDecimal montantFacturesEnRetard;

    // Clients
    private Long nombreClients;
    private Long nombreNouveauxClients;
    private BigDecimal montantMoyenParClient;

    // Produits
    private Long nombreProduits;
    private Long nombreProduitsActifs;
    private Long nombreProduitsAlerteStock;

    // Stock
    private BigDecimal valeurTotaleStock;
    private Long nombreMouvementsStock;

    // Achats
    private BigDecimal montantAchatsMois;
    private Long nombreBonsCommande;
    private Long nombreBonsAchat;

    // Trésorerie
    private BigDecimal soldeTresorerie;
    private BigDecimal encaissementsPrevus;
    private BigDecimal decaissementsPrevus;

    // Top produits/clients
    private List<ProduitStats> topProduits;
    private List<ClientStats> topClients;

    // Évolution mensuelle
    private List<EvolutionMensuelle> evolutionCA12Mois;

    private LocalDate dateGeneration;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProduitStats {
        private String nomProduit;
        private String reference;
        private Long quantiteVendue;
        private BigDecimal chiffreAffaires;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientStats {
        private String nomClient;
        private Long nombreFactures;
        private BigDecimal montantTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolutionMensuelle {
        private String periode;
        private BigDecimal montant;
    }
}
