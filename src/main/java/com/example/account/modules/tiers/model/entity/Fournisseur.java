package com.example.account.modules.tiers.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.tiers.model.enums.TypeClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "fournisseurs",
    indexes = {
        @Index(name = "idx_fournisseur_org", columnList = "organization_id"),
        @Index(name = "idx_fournisseur_org_username", columnList = "organization_id, username")
    }
)
public class Fournisseur extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_fournisseur")
    private UUID idFournisseur;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column(name = "username")
    private String username;

    @NotBlank(message = "La catégorie est obligatoire")
    @Column(name = "categorie")
    private String categorie;

    @Column(name = "site_web")
    private String siteWeb;

    @Column(name = "n_tva")
    @Builder.Default
    private Boolean nTva = false;

    @NotBlank(message = "L'adresse est obligatoire")
    @Column(name = "adresse")
    private String adresse;

    @Column(name = "telephone")
    private String telephone;

    @Email(message = "Format d'email invalide")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Le type de fournisseur est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_fournisseur")
    private TypeClient typeFournisseur;

    @Column(name = "raison_sociale")
    private String raisonSociale;

    @Column(name = "numero_tva")
    private String numeroTva;

    @Column(name = "code_fournisseur")
    private String codeFournisseur;

    @Column(name = "limite_credit")
    private Double limiteCredit;

    @Column(name = "solde_courant")
    @Builder.Default
    private Double soldeCourant = 0.0;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}