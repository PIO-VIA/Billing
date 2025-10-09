package com.example.account
.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class TableauBordResponse {
    
    private UUID id;
    
    private String nom;
    
    private String description;
    
    private UUID utilisateur;
    
    private String nomUtilisateur;
    
    private boolean publicTableau;
    
    private boolean defaut;
    
    private String theme;
    
    private Integer largeur;
    
    private Integer hauteur;
    
    private String couleurFond;
    
    private List<String> tags;
    
    private String categorie;
    
    private Integer ordre;
    
    private boolean actif;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateModification;
    
    private UUID crePar;
    
    private UUID modifiePar;
    
    private List<WidgetResponse> widgets;
    
    private Integer nombreVues;
    
    private LocalDateTime dernierAcces;
    
    private String statut;
    
    private Map<String, Object> parametres;
}