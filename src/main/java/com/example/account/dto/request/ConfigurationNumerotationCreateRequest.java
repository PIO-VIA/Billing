package com.example.account
.dto.request;

import com.example.account
.model.enums.TypeNumerotation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ConfigurationNumerotationCreateRequest {
    
    @NotBlank(message = "Le nom de la configuration est obligatoire")
    private String nom;
    
    private String description;
    
    @NotNull(message = "Le type de numérotation est obligatoire")
    private TypeNumerotation typeNumerotation;
    
    @NotBlank(message = "Le préfixe est obligatoire")
    private String prefixe;
    
    private String suffixe;
    
    @NotNull(message = "La longueur minimale est obligatoire")
    private Integer longueurMinimale;
    
    private Integer longueurMaximale;
    
    private String separateur;
    
    private String formatDate;
    
    private boolean includeAnnee = true;
    
    private boolean includeMois = false;
    
    private boolean includeJour = false;
    
    private Integer valeurInitiale = 1;
    
    private Integer increment = 1;
    
    private boolean reinitialiserAnnuellement = true;
    
    private boolean reinitialiserMensuellement = false;
    
    private String regexValidation;
    
    private Map<String, Object> parametresAvances;
    
    private boolean actif = true;
    
    private Integer ordre = 1;
}