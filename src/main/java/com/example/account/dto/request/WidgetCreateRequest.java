package com.example.account
.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class WidgetCreateRequest {
    
    @NotBlank(message = "Le nom du widget est obligatoire")
    private String nom;
    
    @NotBlank(message = "Le type de widget est obligatoire")
    private String typeWidget;
    
    @NotNull(message = "L'ID du tableau de bord est obligatoire")
    private UUID idTableauBord;
    
    private String description;
    
    @NotNull(message = "La position X est obligatoire")
    private Integer positionX;
    
    @NotNull(message = "La position Y est obligatoire")
    private Integer positionY;
    
    @NotNull(message = "La largeur est obligatoire")
    private Integer largeur;
    
    @NotNull(message = "La hauteur est obligatoire")
    private Integer hauteur;
    
    private String couleurFond;
    
    private String couleurTexte;
    
    private String sourceDonnees;
    
    private String requete;
    
    private Map<String, Object> parametres;
    
    private Map<String, Object> optionsAffichage;
    
    private Integer ordreAffichage;
    
    private boolean actif = true;
    
    private Integer intervalleActualisation;
    
    private boolean cacheActive = true;
    
    private Integer dureeCache;
}