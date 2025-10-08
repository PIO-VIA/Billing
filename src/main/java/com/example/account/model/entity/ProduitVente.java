package com.yooyob.erp.model.entity;

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
@Table(name = "produits_vente")
public class ProduitVente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_produit")
    private UUID idProduit;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Column(name = "nom_produit")
    private String nomProduit;

    @Column(name = "type_produit")
    private String typeProduit;

    @NotNull(message = "Le prix de vente est obligatoire")
    @PositiveOrZero(message = "Le prix de vente doit être positif ou nul")
    @Column(name = "prix_vente")
    private BigDecimal prixVente;

    @Column(name = "cout")
    private BigDecimal cout;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "reference")
    private String reference;

    @Column(name = "code_barre")
    private String codeBarre;

    @Column(name = "photo")
    private String photo;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
