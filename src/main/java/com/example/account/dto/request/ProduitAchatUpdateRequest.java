package com.example.account
.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitAchatUpdateRequest {

    private String nomProduit;

    private String reference;

    private String codeBarre;

    private String typeProduit;

    private String categorie;

    private String description;

    private String unite;

    @PositiveOrZero(message = "Le prix d'achat doit être positif ou nul")
    private BigDecimal prixAchat;

    private Boolean active;

    private String marque;

    private String modele;

    private String couleur;

    private String taille;

    private Double poids;

    private String dimensions;

    private String specifications;

    private String imageUrl;

    private Integer stockMinimum;

    private Integer stockActuel;

    private String emplacementStock;

    private String fournisseurPrincipal;

    private String notes;
}