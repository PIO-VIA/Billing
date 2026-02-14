package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.entity.Lines.LineBonCommande;
import com.example.account.modules.facturation.model.enums.StatusBonCommande;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table("bons_commande")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BonCommande extends OrganizationScoped {

    @Id
    @Column("id_bon_commande")
    private UUID idBonCommande;

    @Column("numero_commande")
    private String numeroCommande;

    @Column("id_client")
    private UUID idClient;

    @Column("nom_client")
    private String nomClient;

    @Column("adresse_client")
    private String adresseClient;

    @Column("email_client")
    private String emailClient;

    @Column("telephone_client")
    private String telephoneClient;

    @Column("recipient_name")
    private String recipientName;

    @Column("recipient_phone")
    private String recipientPhone;

    @Column("recipient_address")
    private String recipientAddress;

    @Column("recipient_city")
    private String recipientCity;

    @Column("id_devis_origine")
    private UUID idDevisOrigine;

    @Column("numero_devis_origine")
    private String numeroDevisOrigine;

    @Column("nos_ref")
    private String nosRef;

    @Column("vos_ref")
    private String vosRef;

    @Column("date_commande")
    private LocalDateTime dateCommande;

    @Column("date_systeme")
    private LocalDateTime dateSysteme;

    @Column("date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    @Column("lines")
    private List<LineBonCommande> lines;

    @Column("montant_ht")
    private BigDecimal montantHT;

    @Column("montant_tva")
    private BigDecimal montantTVA;

    @Column("montant_ttc")
    private BigDecimal montantTTC;

    @Column("devise")
    private String devise;

    @Column("apply_vat")
    private Boolean applyVat;

    @Column("transport_method")
    private String transportMethod;

    @Column("id_agency")
    private UUID idAgency;

    @Column("mode_reglement")
    private String modeReglement;

    @Column("statut")
    private StatusBonCommande statut;

    @Column("notes")
    private String notes;

    @Column("created_by")
    private UUID createdBy;

    @Column("validated_by")
    private UUID validatedBy;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("validated_at")
    private LocalDateTime validatedAt;
}