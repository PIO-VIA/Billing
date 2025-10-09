package com.example.account
.dto.response;

import com.example.account
.model.enums.TypeEscompte;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class EscomptePaiementResponse {
    
    private UUID id;
    
    private String nom;
    
    private String description;
    
    private TypeEscompte typeEscompte;
    
    private BigDecimal tauxEscompte;
    
    private BigDecimal montantFixe;
    
    private BigDecimal montantMinimum;
    
    private BigDecimal montantMaximum;
    
    private Integer delaiPaiementJours;
    
    private LocalDate dateDebutValidite;
    
    private LocalDate dateFinValidite;
    
    private List<UUID> clientsEligibles;
    
    private List<String> nomClientsEligibles;
    
    private List<String> categoriesClientEligibles;
    
    private List<UUID> produitsEligibles;
    
    private List<String> nomProduitsEligibles;
    
    private List<String> categoriesProduitEligibles;
    
    private boolean cumulable;
    
    private boolean automatique;
    
    private Map<String, Object> conditions;
    
    private Integer priorite;
    
    private boolean actif;
    
    private String codePromotionnel;
    
    private Integer nombreUtilisationsMax;
    
    private Integer nombreUtilisations;
    
    private BigDecimal montantUtilise;
    
    private BigDecimal plafondMensuel;
    
    private String periodicite;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateModification;
    
    private UUID crePar;
    
    private UUID modifiePar;
    
    private String nomCreateur;
    
    private String nomModificateur;
    
    private String statut; // ACTIF, INACTIF, EXPIRE, SUSPENDU
}