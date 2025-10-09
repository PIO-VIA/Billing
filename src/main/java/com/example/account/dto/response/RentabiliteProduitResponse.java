package com.example.account
.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentabiliteProduitResponse {

    private UUID idProduit;

    private String nomProduit;

    private String codeProduit;

    private String categorieProduit;

    private Integer quantiteVendue;

    private Integer quantiteVendueAnneePrec;

    private BigDecimal croissanceQuantite;

    private BigDecimal chiffreAffaires;

    private BigDecimal chiffreAffairesAnneePrec;

    private BigDecimal croissanceChiffreAffaires;

    private BigDecimal coutTotal;

    private BigDecimal coutUnitaireMoyen;

    private BigDecimal cout_moy_unitaire;

    private BigDecimal margeUnitaire;

    private BigDecimal margeTotale;

    private BigDecimal tauxMarge;

    private BigDecimal prix_moy_vente;

    private BigDecimal prixVenteMin;

    private BigDecimal prixVenteMax;

    private BigDecimal remiseMoyenne;

    private Integer nombreClientsAcheteurs;

    private Integer nombreFacturesContenant;

    private BigDecimal rotationStock;

    private Integer stockActuel;

    private Integer stockMinimal;

    private String statusStock; // DISPONIBLE, RUPTURE, FAIBLE

    private BigDecimal partChiffreAffairesTotal;

    private String performanceCategorie; // TOP, BON, MOYEN, FAIBLE

    private BigDecimal tendanceVente; // CROISSANTE, STABLE, DECROISSANTE

    private LocalDate dateDerniereVente;

    private String saisonnalite;

    private BigDecimal elasticitePrix;

    private String commentaire;
}