package com.yooyob.erp.model.entity;

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
@Table(name = "remboursements")
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_remboursement")
    private UUID idRemboursement;

    @NotNull(message = "La date de facturation est obligatoire")
    @Column(name = "date_facturation")
    private LocalDate dateFacturation;

    @NotNull(message = "La date comptable est obligatoire")
    @Column(name = "date_comptable")
    private LocalDate dateComptable;

    @Column(name = "reference_paiement")
    private String referencePaiement;

    @Column(name = "banque_destination")
    private String banqueDestination;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Column(name = "montant")
    private BigDecimal montant;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column(name = "motif")
    private String motif;

    @Column(name = "numero_piece")
    private String numeroPiece;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "EN_ATTENTE";

    @Column(name = "id_facture")
    private UUID idFacture;

    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}