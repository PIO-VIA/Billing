package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "produits_achat")
public class ProduitAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_produit")
    private UUID idProduit;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Column(name = "nom_produit")
    private String nomProduit;

    @Column(name = "type_produit")
    private String typeProduit;

    @NotNull(message = "Le prix d'achat est obligatoire")
    @PositiveOrZero(message = "Le prix d'achat doit être positif ou nul")
    @Column(name = "prix_achat")
    private BigDecimal prixAchat;

    @Column(name = "cout_standard")
    private BigDecimal coutStandard;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "reference")
    private String reference;

    @Column(name = "code_barre")
    private String codeBarre;

    @Column(name = "description")
    private String description;

    @Column(name = "fournisseur_principal")
    private UUID fournisseurPrincipal;

    @Column(name = "stock_minimum")
    @Builder.Default
    private Integer stockMinimum = 0;

    @Column(name = "stock_actuel")
    @Builder.Default
    private Integer stockActuel = 0;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}