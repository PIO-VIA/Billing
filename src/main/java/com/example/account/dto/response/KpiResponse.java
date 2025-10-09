package com.example.account
.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class KpiResponse {
    
    private String nom;
    
    private String code;
    
    private BigDecimal valeur;
    
    private String unite;
    
    private String typeValeur; // MONTANT, POURCENTAGE, NOMBRE, DUREE
    
    private BigDecimal valeurPrecedente;
    
    private BigDecimal evolution;
    
    private String tendance; // HAUSSE, BAISSE, STABLE
    
    private String couleur;
    
    private String icone;
    
    private String description;
    
    private LocalDateTime dateCalcul;
    
    private String periodeReference;
    
    private Map<String, Object> metadonnees;
    
    private String formatAffichage;
    
    private Integer precision;
    
    private String statut; // OK, ALERTE, CRITIQUE
    
    private BigDecimal seuilAlerte;
    
    private BigDecimal seuilCritique;
    
    private String commentaire;
}