package com.example.account
.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class WidgetResponse {
    
    private UUID id;
    
    private String nom;
    
    private String typeWidget;
    
    private UUID idTableauBord;
    
    private String nomTableauBord;
    
    private String description;
    
    private Integer positionX;
    
    private Integer positionY;
    
    private Integer largeur;
    
    private Integer hauteur;
    
    private String couleurFond;
    
    private String couleurTexte;
    
    private String sourceDonnees;
    
    private String requete;
    
    private Map<String, Object> parametres;
    
    private Map<String, Object> optionsAffichage;
    
    private Integer ordreAffichage;
    
    private boolean actif;
    
    private Integer intervalleActualisation;
    
    private boolean cacheActive;
    
    private Integer dureeCache;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateModification;
    
    private UUID crePar;
    
    private UUID modifiePar;
    
    private Map<String, Object> donnees;
    
    private LocalDateTime derniereActualisation;
    
    private String statut;
    
    private String messageErreur;
}