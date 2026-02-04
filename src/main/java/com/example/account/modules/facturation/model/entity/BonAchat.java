package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("bons_achat")
public class BonAchat extends OrganizationScoped {

    @Id
    @Column("id_bon_achat")
    private UUID idBonAchat;

    @NotBlank
    @Column("numero_bon_achat")
    private String numeroBonAchat;

    // --- Informations Fournisseur ---
    @Column("supplier_id")
    private UUID supplierId;
    
    @Column("supplier_name")
    private String supplierName;

    @Column("supplier_code")
    private String supplierCode;

    @Column("supplier_email")
    private String supplierEmail;

    @Column("supplier_contact")
    private String supplierContact;

    @Column("supplier_address")
    private String supplierAddress;

    // --- Informations de Livraison ---
    @Column("delivery_name")
    private String deliveryName;

    @Column("delivery_address")
    private String deliveryAddress;

    @Column("delivery_email")
    private String deliveryEmail;

    @Column("delivery_contact")
    private String deliveryContact;

    // --- Dates ---
    @Column("date_bon_achat")
    private LocalDateTime dateBonAchat;

    @Column("date_systeme")
    private LocalDateTime dateSysteme;

    @Column("date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    // --- Transport & Statut ---
    @Column("transport_method")
    private String transportMethod;

    @Column("instructions_livraison")
    private String instructionsLivraison;

    @Column("status")
    private StatutBonAchat status;

    // --- STOCKAGE JSON DES LIGNES ---
    @Column("lines")
    private List<LigneBonAchat> lines;

    // --- Totaux ---
    @Column("subtotal_amount")
    private BigDecimal subtotalAmount;

    @Column("tax_amount")
    private BigDecimal taxAmount;

    @Column("grand_total")
    private BigDecimal grandTotal;

    // --- Audit ---
    @Column("prepared_by")
    private UUID preparedBy;

    @Column("approved_by")
    private UUID approvedBy;

    @Column("remarks")
    private String remarks;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}