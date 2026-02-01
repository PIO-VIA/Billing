package com.example.account.modules.facturation.dto.request;

import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonAchatRequest {

    private String numeroBonAchat;

    @NotNull(message = "L'ID fournisseur est obligatoire")
    private UUID idFournisseur;

    private String nomFournisseur;
    private String transporteurSociete;
    private String numeroVehicule;
    private UUID idBonCommande;
    private String numeroCommande;

    @NotNull(message = "La date de réception est obligatoire")
    private LocalDate dateReception;

    @NotNull(message = "La date du document est obligatoire")
    private LocalDate dateDocument;

    private LocalDate dateSysteme;
    private StatutBonAchat statut;
    private String preparePar;
    private String inspectePar;
    private String approuvePar;
    private String remarques;

    private List<LigneBonAchatRequest> lignes;
}
