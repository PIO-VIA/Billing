package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "lignes_bon_achat",
    indexes = {
        @Index(name = "idx_lignebonachat_org", columnList = "organization_id"),
        @Index(name = "idx_lignebonachat_bonachat", columnList = "id_bon_achat")
    }
)
public class LigneBonAchat extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ligne_bon_achat")
    private UUID idLigneBonAchat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bon_achat", nullable = false)
    private BonAchat bonAchat;

    @NotNull(message = "L'ID produit est obligatoire")
    @Column(name = "id_produit")
    private UUID idProduit;

    @Column(name = "description")
    private String description;

    @Column(name = "unite_mesure")
    private String uniteMesure;

    @Column(name = "quantite_commandee")
    private Integer quantiteCommandee;

    @Column(name = "quantite_recue")
    private Integer quantiteRecue;

    @Column(name = "quantite_acceptee")
    private Integer quantiteAcceptee;

    @Column(name = "quantite_rejetee")
    private Integer quantiteRejetee;

    @Column(name = "quantite_manquante")
    private Integer quantiteManquante;

    @Column(name = "quantite_endommagee")
    private Integer quantiteEndommagee;

    @Column(name = "quantite_excedent")
    private Integer quantiteExcedent;

    @Column(name = "tarif")
    private BigDecimal tarif;

    @Column(name = "remise")
    private BigDecimal remise;

    @Column(name = "montant_ligne")
    private BigDecimal montantLigne;
}
