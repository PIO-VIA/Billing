package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.entity.Lines.LineBonCommande;
import com.example.account.modules.facturation.model.enums.StatusBonCommande; // Adaptez selon votre enum


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
@Table(name = "bons_commande")
public class BonCommande extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idBonCommande;

    @Column(name = "numero_commande", unique = true)
    private String numeroCommande;

    // --- Client Info (Billing) ---
    private UUID idClient;
    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;

    // --- Recipient Info (Shipping) ---
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientCity;

    // --- Source Reference ---
    private UUID idDevisOrigine;
    private String numeroDevisOrigine;
    private String nosRef;
    private String vosRef;

    // --- Dates ---
    private LocalDateTime dateCommande;
    private LocalDateTime dateSysteme;
    private LocalDateTime dateLivraisonPrevue;

    // --- Lines (JSON) ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lines", columnDefinition = "jsonb")
    private List<LineBonCommande> lines;

    // --- Financials ---
    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;
    private Boolean applyVat;

    // --- Logistics ---
    private String transportMethod; // Ou Enum
    private UUID idAgency;
    private String modeReglement;

    // --- Status ---
    @Enumerated(value = EnumType.STRING)
    private StatusBonCommande statut; // Ou Enum SalesOrderStatus

    // --- Metadata & Audit ---
    @Column(length = 1000)
    private String notes;
    private UUID createdBy;
    private UUID validatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime validatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}