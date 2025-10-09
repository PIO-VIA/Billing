package com.example.account
.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class RapportResponse {
    
    private UUID id;
    
    private String nom;
    
    private String typeRapport; // EXECUTIF, COMMERCIAL, FINANCIER, PERSONNALISE
    
    private String description;
    
    private LocalDateTime dateGeneration;
    
    private UUID generePar;
    
    private String nomGenerateur;
    
    private String periodeDebut;
    
    private String periodeFin;
    
    private String statut; // EN_COURS, TERMINE, ERREUR
    
    private Map<String, Object> parametres;
    
    private List<Map<String, Object>> sections;
    
    private List<KpiResponse> kpis;
    
    private List<Map<String, Object>> graphiques;
    
    private List<Map<String, Object>> tableaux;
    
    private Map<String, Object> resumeExecutif;
    
    private List<String> conclusions;
    
    private List<String> recommandations;
    
    private String formatExport; // PDF, EXCEL, CSV, JSON
    
    private String cheminFichier;
    
    private Long tailleFichier;
    
    private Integer nombrePages;
    
    private LocalDateTime dateExpiration;
    
    private List<String> destinataires;
    
    private boolean envoye;
    
    private LocalDateTime dateEnvoi;
    
    private Map<String, Object> metadonnees;
}