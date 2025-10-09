package com.example.account
.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendanceVenteResponse {

    private String periode;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private BigDecimal chiffreAffaires;

    private Integer nombreVentes;

    private BigDecimal panierMoyen;

    private BigDecimal croissancePeriodePrecedente;

    private BigDecimal croissanceAnneePrec;

    private Integer nombreClientsActifs;

    private Integer nombreNouveauxClients;

    private BigDecimal tauxFidelisation;

    private BigDecimal montantMoyenParClient;

    private BigDecimal margeGlobale;

    private BigDecimal tauxMarge;

    private Integer nombreProduitsVendus;

    private BigDecimal volumeTotal;

    private String unite;

    private String typePeriode; // JOUR, SEMAINE, MOIS, TRIMESTRE, ANNEE

    private BigDecimal previsionProchainePeriode;

    private BigDecimal ecartPrevisionReelle;

    private String commentaire;
}