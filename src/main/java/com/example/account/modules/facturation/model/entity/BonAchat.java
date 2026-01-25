package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    name = "bons_achat",
    indexes = {
        @Index(name = "idx_bonachat_org", columnList = "organization_id"),
        @Index(name = "idx_bonachat_org_numero", columnList = "organization_id, numero_bon_achat")
    }
)
public class BonAchat extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bon_achat")
    private UUID idBonAchat;

    @NotBlank(message = "Le numéro de bon d'achat est obligatoire")
    @Column(name = "numero_bon_achat", unique = true)
    private String numeroBonAchat;

    @NotNull(message = "L'ID fournisseur est obligatoire")
    @Column(name = "id_fournisseur")
    private UUID idFournisseur;

    @Column(name = "nom_fournisseur")
    private String nomFournisseur;

    @Column(name = "transporteur_societe")
    private String transporteurSociete;

    @Column(name = "numero_vehicule")
    private String numeroVehicule;

    @Column(name = "id_bon_commande")
    private UUID idBonCommande;

    @Column(name = "numero_commande")
    private String numeroCommande;

    @NotNull(message = "La date de réception est obligatoire")
    @Column(name = "date_reception")
    private LocalDate dateReception;

    @NotNull(message = "La date du document est obligatoire")
    @Column(name = "date_document")
    private LocalDate dateDocument;

    @Column(name = "date_systeme")
    private LocalDate dateSysteme;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutBonAchat statut;

    @Column(name = "prepare_par")
    private String preparePar;

    @Column(name = "inspecte_par")
    private String inspectePar;

    @Column(name = "approuve_par")
    private String approuvePar;

    @Column(name = "remarques", length = 1000)
    private String remarques;

    @OneToMany(mappedBy = "bonAchat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneBonAchat> lignes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
