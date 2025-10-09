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
public class PerformanceClientResponse {

    private UUID idClient;

    private String nomClient;

    private String emailClient;

    private String typeClient;

    private BigDecimal chiffreAffaires;

    private BigDecimal chiffreAffairesAnneePrec;

    private BigDecimal croissanceChiffreAffaires;

    private Integer nombreCommandes;

    private Integer nombreCommandesAnneePrec;

    private BigDecimal panierMoyen;

    private BigDecimal panierMoyenAnneePrec;

    private BigDecimal croissancePanierMoyen;

    private Integer delaiMoyenPayement;

    private BigDecimal tauxPayement;

    private BigDecimal tauxRetour;

    private BigDecimal scoreFidelite;

    private Integer ancienneteRelation; // en mois

    private Integer frequenceCommande; // jours entre commandes

    private LocalDate dernierAchat;

    private LocalDate premierAchat;

    private BigDecimal margeRealisee;

    private BigDecimal tauxMarge;

    private Integer nombreProduitsAchetes;

    private String categorieClient; // VIP, PREMIUM, STANDARD, OCCASIONNEL

    private BigDecimal potentielCroissance;

    private String risqueCredit; // FAIBLE, MOYEN, ELEVE

    private BigDecimal limiteCredit;

    private BigDecimal creditUtilise;

    private Integer nombreRelances;

    private Integer scoreEngagement;

    private String commentaire;
}