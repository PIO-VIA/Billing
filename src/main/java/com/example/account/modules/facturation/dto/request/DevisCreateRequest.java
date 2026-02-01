package com.example.account.modules.facturation.dto.request;

import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.model.enums.TypePaiementDevis;
import com.example.account.modules.facturation.model.enums.TypePaiementDevis;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevisCreateRequest {

    private String numeroDevis; // Often auto-generated, but included if client specifies

    @NotNull(message = "La date de création est obligatoire")
    private LocalDateTime dateCreation;

    @NotNull(message = "La date de validité est obligatoire")
    private LocalDateTime dateValidite;

    private String type;

    private StatutDevis statut;

    @NotNull(message = "L'ID client est obligatoire")
    private String idClient; // String to match TS Mock 'c001' or UUID strings

    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;

    @Valid
    private List<LigneDevisCreateRequest> lignesDevis;

    // Financial Summary Fields
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private BigDecimal montantTotal;
    private BigDecimal finalAmount;

    private String devise;

    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    private String conditionsPaiement;
    private String notes;
    private String referenceExterne;

    @PositiveOrZero(message = "La remise globale en pourcentage doit être positive ou nulle")
    private BigDecimal remiseGlobalePourcentage;

    @PositiveOrZero(message = "La remise globale en montant doit être positive ou nulle")
    private BigDecimal remiseGlobaleMontant;

    @Builder.Default
    private Integer validiteOffreJours = 30;

    // --- Added Fields from UpdatedDevisResponse ---

    @NotNull(message = "Le flag de TVA est obligatoire")
    private Boolean applyVat;

    private LocalDateTime dateSysteme;

    private TypePaiementDevis modeReglement;

    private String nosRef;

    private String vosRef;

    private Integer nbreEcheance;

    private String referalClientId;
    
    private String pdfPath;
    private UUID organizationId;
    
}