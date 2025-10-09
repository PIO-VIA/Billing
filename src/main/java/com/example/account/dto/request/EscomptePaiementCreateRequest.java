package com.example.account
.dto.request;

import com.example.account
.model.enums.TypeEscompte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class EscomptePaiementCreateRequest {
    
    @NotBlank(message = "Le nom de l'escompte est obligatoire")
    private String nom;
    
    private String description;
    
    @NotNull(message = "Le type d'escompte est obligatoire")
    private TypeEscompte typeEscompte;
    
    @NotNull(message = "Le taux d'escompte est obligatoire")
    @DecimalMin(value = "0.0", message = "Le taux d'escompte doit être positif")
    private BigDecimal tauxEscompte;
    
    private BigDecimal montantFixe;
    
    private BigDecimal montantMinimum;
    
    private BigDecimal montantMaximum;
    
    private Integer delaiPaiementJours;
    
    private LocalDate dateDebutValidite;
    
    private LocalDate dateFinValidite;
    
    private List<UUID> clientsEligibles;
    
    private List<String> categoriesClientEligibles;
    
    private List<UUID> produitsEligibles;
    
    private List<String> categoriesProduitEligibles;
    
    private boolean cumulable = false;
    
    private boolean automatique = false;
    
    private Map<String, Object> conditions;
    
    private Integer priorite = 1;
    
    private boolean actif = true;
    
    private String codePromotionnel;
    
    private Integer nombreUtilisationsMax;
    
    private BigDecimal plafondMensuel;
    
    private String periodicite; // UNIQUE, MENSUEL, TRIMESTRIEL, ANNUEL
}