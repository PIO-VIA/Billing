package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @NotNull(message = "La date de livraison est obligatoire")
    @Column(name = "date_livraison")
    private LocalDate dateLivraison;

    @Column(name = "heure_livraison")
    private String heureLivraison;

    @NotNull(message = "Le client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "id_facture")
    private UUID idFacture;

    @Column(name = "numero_facture")
    private String numeroFacture;

    @Column(name = "id_bon_commande")
    private UUID idBonCommande;

    @Column(name = "numero_commande")
    private String numeroCommande;

    @Column(name = "adresse_livraison", length = 500)
    private String adresseLivraison;

    @Column(name = "ville_livraison")
    private String villeLivraison;

    @Column(name = "code_postal_livraison")
    private String codePostalLivraison;

    @Column(name = "pays_livraison")
    private String paysLivraison;

    @Column(name = "contact_livraison")
    private String contactLivraison;

    @Column(name = "telephone_livraison")
    private String telephoneLivraison;

    @Column(name = "transporteur")
    private String transporteur;

    @Column(name = "numero_suivi")
    private String numeroSuivi;

    @Column(name = "mode_livraison")
    private String modeLivraison;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "EN_PREPARATION";

    @Column(name = "nombre_colis")
    private Integer nombreColis;

    @Column(name = "poids_total")
    private String poidsTotal;

    @Column(name = "signature_client")
    private String signatureClient;

    @Column(name = "date_signature")
    private LocalDateTime dateSignature;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "instructions_speciales", length = 500)
    private String instructionsSpeciales;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "livraison_effectuee")
    @Builder.Default
    private Boolean livraisonEffectuee = false;

    @Column(name = "date_livraison_effective")
    private LocalDateTime dateLivraisonEffective;
}
