package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "bons_livraison",
    indexes = {
        @Index(name = "idx_bonlivraison_org", columnList = "organization_id"),
        @Index(name = "idx_bonlivraison_org_numerobonlivraison", columnList = "organization_id, numero_bon_livraison")
    }
)
public class BonLivraison extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bon_livraison")
    private UUID idBonLivraison;

    @NotBlank(message = "Le numéro de bon de livraison est obligatoire")
    @Column(name = "numero_bon_livraison", unique = true)
    private String numeroBonLivraison;

    @NotNull(message = "Le client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "nom_client")
    private String nomClient;

    // Receiver Information
    @Column(name = "nom_destinataire")
    private String nomDestinataire;

    @Column(name = "adresse_destinataire")
    private String adresseDestinataire;

    @Column(name = "contact_destinataire")
    private String contactDestinataire;

    // Agency / Pickup Address
    @Column(name = "nom_agence")
    private String nomAgence;

    @Column(name = "adresse_agence")
    private String adresseAgence;

    @Column(name = "contact_agence")
    private String contactAgence;

    @NotNull(message = "La date de livraison est obligatoire")
    @Column(name = "date_livraison")
    private LocalDate dateLivraison;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "id_facture")
    private UUID idFacture;

    @Column(name = "numero_facture")
    private String numeroFacture;

    @Column(name = "id_bon_commande")
    private UUID idBonCommande;

    @Column(name = "numero_commande")
    private String numeroCommande;

    @Column(name = "statut")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutBonLivraison statut = StatutBonLivraison.EN_PREPARATION;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lignes", columnDefinition = "jsonb") 
    @Builder.Default
    private List<LigneBonLivraison> lignes = new ArrayList<>();

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "conditions_generales", length = 1000)
    private String conditionsGenerales;

    @Column(name = "transporteur")
    private String transporteur;

    @Column(name = "numero_suivi")
    private String numeroSuivi;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "livraison_effectuee")
    @Builder.Default
    private Boolean livraisonEffectuee = false;

    @Column(name = "date_livraison_effective")
    private LocalDateTime dateLivraisonEffective;
}

