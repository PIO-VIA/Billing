package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.entity.Lines.LineBonCommande;
import com.example.account.modules.facturation.model.enums.StatusBonCommande;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("bons_commande")
public class BonCommande extends OrganizationScoped {

    @Id
    @Column("id_bon_commande")
    private UUID idBonCommande;

    @Column("numero_commande")
    private String numeroCommande;

    // --- Client Info (Billing) ---
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

    // --- Recipient Info (Shipping) ---
    @Column("recipient_name")
    private String recipientName;
    
    @Column("recipient_phone")
    private String recipientPhone;
    
    @Column("recipient_address")
    private String recipientAddress;
    
    @Column("recipient_city")
    private String recipientCity;

    // --- Source Reference ---
    @Column("id_devis_origine")
    private UUID idDevisOrigine;
    
    @Column("numero_devis_origine")
    private String numeroDevisOrigine;
    
    @Column("nos_ref")
    private String nosRef;
    
    @Column("vos_ref")
    private String vosRef;

    // --- Dates ---
    @Column("date_commande")
    private LocalDateTime dateCommande;
    
    @Column("date_systeme")
    private LocalDateTime dateSysteme;
    
    @Column("date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    // --- Lines (JSON) ---
    @Column("lines")
    private List<LineBonCommande> lines;

    // --- Financials ---
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

    // --- Logistics ---
    @Column("transport_method")
    private String transportMethod;
    
    @Column("id_agency")
    private UUID idAgency;
    
    @Column("mode_reglement")
    private String modeReglement;

    // --- Status ---
    @Column("statut")
    private StatusBonCommande statut;

    // --- Metadata & Audit ---
    @Column("notes")
    private String notes;
    
    @Column("created_by")
    private UUID createdBy;
    
    @Column("validated_by")
    private UUID validatedBy;

    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
    
    @Column("validated_at")
    private LocalDateTime validatedAt;
}