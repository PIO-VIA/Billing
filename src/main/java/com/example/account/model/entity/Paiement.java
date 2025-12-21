package com.example.account.model.entity;

import com.example.account.model.enums.TypePaiement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "paiements",
    indexes = {
        @Index(name = "idx_paiement_org", columnList = "organization_id"),
        @Index(name = "idx_paiement_org_client", columnList = "organization_id, id_client"),
        @Index(name = "idx_paiement_org_facture", columnList = "organization_id, id_facture")
    }
)
public class Paiement extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_paiement")
    private UUID idPaiement;

    @NotNull(message = "L'ID client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Column(name = "montant")
    private BigDecimal montant;

    @NotNull(message = "La date est obligatoire")
    @Column(name = "date")
    private LocalDate date;

    @NotNull(message = "Le journal est obligatoire")
    @Column(name = "journal")
    private String journal;

    @NotNull(message = "Le mode de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement")
    private TypePaiement modePaiement;

    @Column(name = "compte_bancaire_f")
    private String compteBancaireF;

    @Column(name = "memo")
    private String memo;

    @Column(name = "id_facture")
    private UUID idFacture;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
