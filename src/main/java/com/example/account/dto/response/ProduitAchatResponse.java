package com.example.account
.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProduitAchatResponse {

    private UUID idProduit;
    private String nomProduit;
    private String reference;
    private String codeBarre;
    private String typeProduit;
    private String categorie;
    private String description;
    private String unite;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}