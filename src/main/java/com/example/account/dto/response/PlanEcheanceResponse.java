package com.example.account
.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class PlanEcheanceResponse {
    
    private UUID idFacture;
    
    private String numeroFacture;
    
    private BigDecimal montantTotal;
    
    private String typeEcheance;
    
    private LocalDate dateFacture;
    
    private List<EcheanceDetailResponse> echeances;
    
    private BigDecimal totalEcheances;
    
    private Integer nombreEcheances;
    
    private LocalDate premiereDateEcheance;
    
    private LocalDate derniereDateEcheance;
    
    private Integer dureeEtalementJours;
    
    private Map<String, Object> parametres;
    
    private String statut; // BROUILLON, VALIDE, APPLIQUE
    
    private String commentaire;
    
    @Data
    public static class EcheanceDetailResponse {
        
        private Integer numeroEcheance;
        
        private LocalDate dateEcheance;
        
        private BigDecimal montant;
        
        private BigDecimal pourcentage;
        
        private String description;
        
        private String typeEcheance;
        
        private Map<String, Object> conditions;
        
        private UUID idEscompteApplicable;
        
        private BigDecimal montantEscompte;
        
        private String statut;
    }
}