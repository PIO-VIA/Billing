package com.example.account.modules.tiers.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.tiers.model.enums.TypeClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fournisseur (Supplier) entity representing a supplier in the system.
 */
@Table("fournisseurs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fournisseur extends OrganizationScoped {

    @Id
    @Column("id_fournisseur")
    private UUID idFournisseur;

    @Column("username")
    private String username;

    @Column("categorie")
    private String categorie;

    @Column("site_web")
    private String siteWeb;

    @Column("n_tva")
    @Builder.Default
    private Boolean nTva = false;

    @Column("adresse")
    private String adresse;

    @Column("telephone")
    private String telephone;

    @Column("email")
    private String email;

    @Column("type_fournisseur")
    private TypeClient typeFournisseur;

    @Column("raison_sociale")
    private String raisonSociale;

    @Column("numero_tva")
    private String numeroTva;

    @Column("code_fournisseur")
    private String codeFournisseur;

    @Column("limite_credit")
    private Double limiteCredit;

    @Column("solde_courant")
    @Builder.Default
    private Double soldeCourant = 0.0;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}