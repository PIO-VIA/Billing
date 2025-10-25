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
@Table(name = "mouvements_stock")
@Index(name = "idx_mouvement_produit", columnList = "id_produit")
@Index(name = "idx_mouvement_date", columnList = "date_mouvement")
@Index(name = "idx_mouvement_type", columnList = "type_mouvement")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_mouvement")
    private UUID idMouvement;

    @NotBlank(message = "Le numéro de mouvement est obligatoire")
    @Column(name = "numero_mouvement", unique = true)
    private String numeroMouvement;

    @NotNull(message = "Le type de mouvement est obligatoire")
    @Column(name = "type_mouvement")
    private String typeMouvement; // ENTREE, SORTIE, TRANSFERT, AJUSTEMENT, INVENTAIRE

    @NotNull(message = "La date de mouvement est obligatoire")
    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement;

    @NotNull(message = "Le produit est obligatoire")
    @Column(name = "id_produit")
    private UUID idProduit;

    @Column(name = "nom_produit")
    private String nomProduit;

    @Column(name = "reference_produit")
    private String referenceProduit;

    @Column(name = "id_stock")
    private UUID idStock;

    @NotNull(message = "La quantité est obligatoire")
    @Column(name = "quantite")
    private BigDecimal quantite;

    @Column(name = "quantite_avant")
    private BigDecimal quantiteAvant;

    @Column(name = "quantite_apres")
    private BigDecimal quantiteApres;

    @Column(name = "unite_mesure")
    private String uniteMesure;

    @Column(name = "cout_unitaire")
    private BigDecimal coutUnitaire;

    @Column(name = "valeur_mouvement")
    private BigDecimal valeurMouvement;

    @Column(name = "emplacement_source")
    private String emplacementSource;

    @Column(name = "emplacement_destination")
    private String emplacementDestination;

    @Column(name = "document_reference")
    private String documentReference; // Numéro facture, bon commande, etc.

    @Column(name = "type_document")
    private String typeDocument; // FACTURE, BON_COMMANDE, BON_LIVRAISON, etc.

    @Column(name = "id_document")
    private UUID idDocument;

    @Column(name = "motif")
    private String motif;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "validated")
    @Builder.Default
    private Boolean validated = false;

    @Column(name = "validated_by")
    private String validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;
}
