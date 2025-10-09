package com.example.account
.dto.response;

import com.example.account
.model.enums.TypeNumerotation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ConfigurationNumerotationResponse {
    
    private UUID id;
    
    private String nom;
    
    private String description;
    
    private TypeNumerotation typeNumerotation;
    
    private String prefixe;
    
    private String suffixe;
    
    private Integer longueurMinimale;
    
    private Integer longueurMaximale;
    
    private String separateur;
    
    private String formatDate;
    
    private boolean includeAnnee;
    
    private boolean includeMois;
    
    private boolean includeJour;
    
    private Integer valeurInitiale;
    
    private Integer increment;
    
    private boolean reinitialiserAnnuellement;
    
    private boolean reinitialiserMensuellement;
    
    private String regexValidation;
    
    private Map<String, Object> parametresAvances;
    
    private boolean actif;
    
    private Integer ordre;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateModification;
    
    private UUID crePar;
    
    private UUID modifiePar;
    
    private String nomCreateur;
    
    private String nomModificateur;
    
    private Integer nombreUtilisations;
    
    private String dernierNumeroGenere;
    
    private LocalDateTime derniereUtilisation;
    
    private String statut;
    
    private String exemple;
}