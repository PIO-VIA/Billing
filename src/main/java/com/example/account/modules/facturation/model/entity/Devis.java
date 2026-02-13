package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.model.enums.TypePaiementDevis;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Entity
@Table(
    name = "devis",
    indexes = {
        @Index(name = "idx_devis_org", columnList = "organization_id"),
        @Index(name = "idx_devis_org_numero", columnList = "organization_id, numero_devis"),
        @Index(name = "idx_devis_org_client", columnList = "organization_id, id_client")
    }
)
public class Devis extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_devis")
    private UUID idDevis;

    @NotBlank(message = "Le numéro de devis est obligatoire")
    @Column(name = "numero_devis")
    private String numeroDevis;

    @NotNull(message = "La date de création est obligatoire")
    private LocalDateTime dateCreation;

    @NotNull(message = "La date de validité est obligatoire")
    private LocalDateTime dateValidite;

    private String type;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    private StatutDevis statut;

    // --- THE JSON FIELD ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lignes_devis", columnDefinition = "jsonb")
    private List<LigneDevis> lignesDevis; 

    
    private BigDecimal montantTotal;

    @NotNull(message = "L'ID client est obligatoire")
    private String idClient;

    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;

    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private String devise;

    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    private String conditionsPaiement;

    @Column(length = 1000)
    private String notes;

    private String referenceExterne;
    private String pdfPath;

    @Builder.Default
    private Boolean envoyeParEmail = false;

    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime dateAcceptation;
    private LocalDateTime dateRefus;
    private String motifRefus;

    private UUID idFactureConvertie;

    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Builder.Default
    private Integer validiteOffreJours = 30;

    // --- NEW FIELDS ---
    @Builder.Default
    private Boolean applyVat = true;

    private LocalDateTime dateSysteme;

    @Enumerated(EnumType.STRING)
    private TypePaiementDevis modeReglement;

    private String nosRef;
    private String vosRef;
    private Integer nbreEcheance;
    private String referalClientId;
    private BigDecimal finalAmount;

    // --- AUDIT ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    @Builder.Default
    private Long version = 0L;

    private UUID organizationId;
    private UUID createdBy;
}