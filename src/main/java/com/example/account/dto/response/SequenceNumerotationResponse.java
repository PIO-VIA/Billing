package com.example.account
.dto.response;

import com.example.account
.model.enums.TypeNumerotation;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SequenceNumerotationResponse {
    
    private UUID id;
    
    private UUID idConfiguration;
    
    private String nomConfiguration;
    
    private TypeNumerotation typeNumerotation;
    
    private String prefixe;
    
    private String suffixe;
    
    private Integer annee;
    
    private Integer mois;
    
    private Integer jour;
    
    private Integer valeurCourante;
    
    private Integer valeurMaximale;
    
    private String dernierNumeroGenere;
    
    private LocalDateTime derniereGeneration;
    
    private boolean verrouillee;
    
    private LocalDateTime dateVerrouillage;
    
    private UUID verrouillePar;
    
    private String statut;
    
    private LocalDate dateCreation;
    
    private LocalDateTime dateModification;
    
    private Integer nombreGenerations;
    
    private String format;
    
    private boolean reinitialiseAnnuellement;
    
    private boolean reinitialiseMenusuellement;
    
    private LocalDate prochainReinitialisation;
    
    private String commentaire;
}