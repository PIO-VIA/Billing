package com.example.account.modules.facturation.dto.response;

import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonAchatResponse {
    private UUID idBonAchat;
    private String numeroBonAchat;
    private UUID idFournisseur;
    private String nomFournisseur;
    private String transporteurSociete;
    private String numeroVehicule;
    private UUID idBonCommande;
    private String numeroCommande;
    private LocalDate dateReception;
    private LocalDate dateDocument;
    private LocalDate dateSysteme;
    private StatutBonAchat statut;
    private List<LigneBonAchatResponse> lignes;
    private String preparePar;
    private String inspectePar;
    private String approuvePar;
    private String remarques;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID organizationId;
}
