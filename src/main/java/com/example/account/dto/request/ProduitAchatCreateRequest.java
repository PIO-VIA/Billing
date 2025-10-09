package com.example.account
.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitAchatCreateRequest {

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nomProduit;

    @NotBlank(message = "La référence est obligatoire")
    private String reference;

    private String codeBarre;

    @NotBlank(message = "Le type de produit est obligatoire")
    private String typeProduit;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    private String description;

    private String unite;

    @NotNull(message = "Le prix d'achat est obligatoire")
    @PositiveOrZero(message = "Le prix d'achat doit être positif ou nul")
    private BigDecimal prixAchat;

    @NotNull(message = "Le statut actif est obligatoire")
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