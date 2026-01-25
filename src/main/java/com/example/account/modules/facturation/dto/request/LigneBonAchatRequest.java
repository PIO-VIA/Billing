package com.example.account.modules.facturation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneBonAchatRequest {

    @NotNull(message = "L'ID produit est obligatoire")
    private UUID idProduit;

    private String description;
    private String uniteMesure;

    private Integer quantiteCommandee;
    private Integer quantiteRecue;
    private Integer quantiteAcceptee;
    private Integer quantiteRejetee;
    private Integer quantiteManquante;
    private Integer quantiteEndommagee;
    private Integer quantiteExcedent;

    private BigDecimal tarif;
    private BigDecimal remise;
    private BigDecimal montantLigne;
}
