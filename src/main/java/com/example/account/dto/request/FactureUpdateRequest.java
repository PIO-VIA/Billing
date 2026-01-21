package com.example.account.dto.request;

import com.example.account.model.enums.StatutFacture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureUpdateRequest {

    private LocalDate dateFacturation;
    private LocalDate dateEcheance;
    private String type;
    private StatutFacture etat;
    private UUID idClient;
    private List<LigneFactureCreateRequest> lignesFacture;
    private String devise;
    private BigDecimal tauxChange;
    private String conditionsPaiement;
    private String notes;
    private String referenceCommande;
    
    /**
     * Global discount percentage (0-100).
     */
    private BigDecimal remiseGlobalePourcentage;
    
    /**
     * Global discount amount.
     */
    private BigDecimal remiseGlobaleMontant;
}