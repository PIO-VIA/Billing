package com.example.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonAchatCreateRequest {

    @NotBlank(message = "Le numéro du bon est obligatoire")
    private String numeroBon;

    @NotNull(message = "La date d'achat est obligatoire")
    private LocalDate dateAchat;

    private LocalDate dateLivraison;

    @NotNull(message = "Le fournisseur est obligatoire")
    private UUID idFournisseur;

    private String nomFournisseur;

    private UUID idBonCommande;

    private String numeroCommandeRef;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    private BigDecimal montantTotal;

    @PositiveOrZero(message = "Le montant HT doit être positif ou nul")
    private BigDecimal montantHT;

    @PositiveOrZero(message = "Le montant TVA doit être positif ou nul")
    private BigDecimal montantTVA;

    @Builder.Default
    private String devise = "EUR";

    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    private String numeroFactureFournisseur;

    private String modePaiement;

    private String conditionsPaiement;

    private String notes;

    private String createdBy;
}
