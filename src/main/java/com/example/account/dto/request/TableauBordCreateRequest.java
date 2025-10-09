package com.example.account
.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TableauBordCreateRequest {
    
    @NotBlank(message = "Le nom du tableau de bord est obligatoire")
    private String nom;
    
    private String description;
    
    @NotNull(message = "L'utilisateur est obligatoire")
    private UUID utilisateur;
    
    private boolean isPublic = false;
    
    private boolean isDefaut = false;
    
    private String theme;
    
    private Integer largeur;
    
    private Integer hauteur;
    
    private String couleurFond;
    
    private List<String> tags;
    
    private String categorie;
    
    private Integer ordre;
    
    private boolean actif = true;
}