package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import com.example.account.modules.facturation.model.enums.ModeReglementNoteCredit;
import com.example.account.modules.facturation.model.enums.StatutNoteCredit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
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
    name = "note_credits",
    indexes = {
        @Index(name = "idx_note_credit_org", columnList = "organization_id"),
        @Index(name = "idx_note_credit_org_numero", columnList = "organization_id, numero_note_credit"),
        @Index(name = "idx_note_credit_org_client", columnList = "organization_id, id_client")
    }
)
public class NoteCredit extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_note_credit")
    private UUID idNoteCredit;

    @NotBlank(message = "Le numéro de note de crédit est obligatoire")
    @Column(name = "numero_note_credit", unique = true)
    private String numeroNoteCredit;

    @Column(name = "numero_facture")
    private String numeroFacture;

    @Column(name = "date_facturation")
    private LocalDateTime dateFacturation;

    @Column(name = "date_echeance")
    private LocalDateTime dateEcheance;

    @Column(name = "date_systeme")
    private LocalDateTime dateSysteme;

    @NotNull(message = "L'état est obligatoire")
    @Enumerated(EnumType.STRING)
    private StatutNoteCredit etat;

    @Builder.Default
    private String type = "AVOIR";

    @NotNull(message = "L'ID client est obligatoire")
    private String idClient;

    private String nomClient;
    private String adresseClient;
    private String emailClient;
    private String telephoneClient;

    private BigDecimal montantHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTTC;
    private BigDecimal montantTotal;
    private BigDecimal montantRestant;
    private BigDecimal finalAmount;

    @Builder.Default
    private BigDecimal remiseGlobalePourcentage = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal remiseGlobaleMontant = BigDecimal.ZERO;

    @Builder.Default
    private Boolean applyVat = true;

    private String devise;

    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    private ModeReglementNoteCredit modeReglement;

    private String conditionsPaiement;
    private Integer nbreEcheance;
    private String nosRef;
    private String vosRef;
    private String referenceCommande;
    private UUID idDevisOrigine;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lignes_facture", columnDefinition = "jsonb")
    private List<LigneNoteCredit> lignesFacture;

    @Column(length = 1000)
    private String notes;

    private String pdfPath;

    @Builder.Default
    private Boolean envoyeParEmail = false;

    private LocalDateTime dateEnvoiEmail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private String referalClientId;

    @Version
    @Builder.Default
    private Long version = 0L;
}
