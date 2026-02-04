package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("bons_livraison")
public class BonLivraison extends OrganizationScoped {

    @Id
    @Column("id_bon_livraison")
    private UUID idBonLivraison;

    @Column("numero_bon_livraison")
    private String numeroBonLivraison;

    @Column("id_client")
    private UUID idClient;

    @Column("nom_client")
    private String nomClient;

    // Receiver Information
    @Column("nom_destinataire")
    private String nomDestinataire;

    @Column("adresse_destinataire")
    private String adresseDestinataire;

    @Column("contact_destinataire")
    private String contactDestinataire;

    // Agency / Pickup Address
    @Column("nom_agence")
    private String nomAgence;

    @Column("adresse_agence")
    private String adresseAgence;

    @Column("contact_agence")
    private String contactAgence;

    @Column("date_livraison")
    private LocalDate dateLivraison;

    @Column("date_echeance")
    private LocalDate dateEcheance;

    @Column("id_facture")
    private UUID idFacture;

    @Column("numero_facture")
    private String numeroFacture;

    @Column("id_bon_commande")
    private UUID idBonCommande;

    @Column("numero_commande")
    private String numeroCommande;

    @Column("statut")
    private StatutBonLivraison statut;

    @org.springframework.data.annotation.Transient
    @Builder.Default
    private java.util.List<LigneBonLivraison> lignes = new java.util.ArrayList<>();

    @Column("montant_total")
    private BigDecimal montantTotal;

    @Column("conditions_generales")
    private String conditionsGenerales;

    @Column("transporteur")
    private String transporteur;

    @Column("numero_suivi")
    private String numeroSuivi;

    @Column("created_by")
    private String createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("livraison_effectuee")
    private Boolean livraisonEffectuee;

    @Column("date_livraison_effective")
    private LocalDateTime dateLivraisonEffective;
}

