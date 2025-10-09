package com.example.account
.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalytiqueFacturationResponse {

    private BigDecimal chiffreAffairesTotalPeriode;

    private Integer nombreFacturesEmises;

    private Integer nombreFacturesPayees;

    private Integer nombreFacturesEnAttente;

    private Integer nombreFacturesEnRetard;

    private Integer nombreFacturesAnnulees;

    private BigDecimal montantTotalFacture;

    private BigDecimal montantTotalPaye;

    private BigDecimal montantTotalEnAttente;

    private BigDecimal montantTotalEnRetard;

    private BigDecimal tauxPayement;

    private Integer delaiMoyenPayement;

    private Integer nombreClientsActifs;

    private Integer nombreNouveauxClients;

    private BigDecimal panierMoyen;

    private BigDecimal croissanceChiffreAffaires;

    private BigDecimal croissanceNombreFactures;

    private BigDecimal croissancePanierMoyen;

    private BigDecimal tauxConversionDevis;

    private BigDecimal montantMoyenFacture;

    private BigDecimal ecartTypeMontantFacture;

    private Integer nombreProduitsVendus;

    private BigDecimal margeGlobale;

    private BigDecimal tauxMarge;

    private BigDecimal recurrenceClientele;

    private LocalDateTime dateCalcul;

    private String periodeAnalyse;

    private String deviseReference;
}