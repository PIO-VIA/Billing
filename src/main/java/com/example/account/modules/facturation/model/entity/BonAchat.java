package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "bons_achat",
    indexes = {
        @Index(name = "idx_bonachat_org", columnList = "organization_id"),
        @Index(name = "idx_bonachat_numero", columnList = "numero_bon_achat")
    }
)
public class BonAchat extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bon_achat")
    private UUID idBonAchat;

    @NotBlank
    @Column(name = "numero_bon_achat", unique = true)
    private String numeroBonAchat;

    // --- Informations Fournisseur ---
    @Column(name = "supplier_id")
    private UUID supplierId;
    
    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_code")
    private String supplierCode;

    @Column(name = "supplier_email")
    private String supplierEmail;

    @Column(name = "supplier_contact")
    private String supplierContact;

    @Column(name = "supplier_address")
    private String supplierAddress;

    // --- Informations de Livraison ---
    @Column(name = "delivery_name")
    private String deliveryName;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "delivery_email")
    private String deliveryEmail;

    @Column(name = "delivery_contact")
    private String deliveryContact;

    // --- Dates (Passées en LocalDateTime) ---
    @Column(name = "date_bon_achat")
    private LocalDateTime dateBonAchat;

    @Column(name = "date_systeme")
    private LocalDateTime dateSysteme;

    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    // --- Transport & Statut ---
    @Column(name = "transport_method")
    private String transportMethod;

    @Column(name = "instructions_livraison")
    private String instructionsLivraison;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatutBonAchat status;

    // --- STOCKAGE JSON DES LIGNES ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lines", columnDefinition = "jsonb")
    private List<LigneBonAchat> lines;

    // --- Totaux ---
    @Column(name = "subtotal_amount", precision = 19, scale = 4)
    private BigDecimal subtotalAmount;

    @Column(name = "tax_amount", precision = 19, scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "grand_total", precision = 19, scale = 4)
    private BigDecimal grandTotal;

    // --- Audit ---
    @Column(name = "prepared_by")
    private UUID preparedBy;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}