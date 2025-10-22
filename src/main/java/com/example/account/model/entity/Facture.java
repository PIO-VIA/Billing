package com.example.account.model.entity;

import com.example.account.model.enums.StatutFacture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_facture")
    private UUID idFacture;

    @NotBlank(message = "Le numéro de facture est obligatoire")
    @Column(name = "numero_facture")
    private String numeroFacture;

    @NotNull(message = "La date de facturation est obligatoire")
    @Column(name = "date_facturation")
    private LocalDate dateFacturation;

    @NotNull(message = "La date d'échéance est obligatoire")
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "type")
    private String type;

    @NotNull(message = "L'état est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "etat")
    private StatutFacture etat;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @NotNull(message = "Le montant restant est obligatoire")
    @PositiveOrZero(message = "Le montant restant doit être positif ou nul")
    @Column(name = "montant_restant")
    private BigDecimal montantRestant;

    @NotNull(message = "L'ID client est obligatoire")
    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "adresse_client")
    private String adresseClient;

    @Column(name = "email_client")
    private String emailClient;

    @Column(name = "telephone_client")
    private String telephoneClient;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneFacture> lignesFacture;

    @Column(name = "montant_ht")
    private BigDecimal montantHT;

    @Column(name = "montant_tva")
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc")
    private BigDecimal montantTTC;

    @Column(name = "devise")
    private String devise;

    @Column(name = "taux_change")
    @Builder.Default
    private BigDecimal tauxChange = BigDecimal.ONE;

    @Column(name = "conditions_paiement")
    private String conditionsPaiement;

    @Column(name = "notes")
    private String notes;

    @Column(name = "reference_commande")
    private String referenceCommande;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "envoye_par_email")
    @Builder.Default
    private Boolean envoyeParEmail = false;

    @Column(name = "date_envoi_email")
    private LocalDateTime dateEnvoiEmail;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}