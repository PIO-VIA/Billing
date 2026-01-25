package com.example.account.modules.facturation.dto.request;

import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import jakarta.validation.constraints.NotNull;
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
public class BonLivraisonRequest {

    private String numeroBonLivraison;

    @NotNull(message = "L'ID client est obligatoire")
    private UUID idClient;

    private String nomClient;
    private String nomDestinataire;
    private String adresseDestinataire;
    private String contactDestinataire;

    private String nomAgence;
    private String adresseAgence;
    private String contactAgence;

    @NotNull(message = "La date de livraison est obligatoire")
    private LocalDate dateLivraison;

    private LocalDate dateEcheance;
    private UUID idFacture;
    private String numeroFacture;
    private UUID idBonCommande;
    private String numeroCommande;
    private StatutBonLivraison statut;

    private List<LigneBonLivraisonRequest> lignes;

    private BigDecimal montantTotal;
    private String conditionsGenerales;
    private String transporteur;
    private String numeroSuivi;
}
