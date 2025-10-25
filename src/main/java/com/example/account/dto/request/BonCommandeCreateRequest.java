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
public class BonCommandeCreateRequest {

    @NotBlank(message = "Le numéro de commande est obligatoire")
    private String numeroCommande;

    @NotNull(message = "La date de commande est obligatoire")
    private LocalDate dateCommande;

    private LocalDate dateLivraisonPrevue;

    @NotNull(message = "Le fournisseur est obligatoire")
    private UUID idFournisseur;

    private String nomFournisseur;

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

    private String referenceExterne;

    private String conditionsPaiement;

    private Integer delaiLivraison;

    private String adresseLivraison;

    private String notes;

    private String createdBy;
}
