package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "historique_facturation_recurrente")
public class HistoriqueFacturationRecurrente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historique")
    private UUID idHistorique;

    @NotNull(message = "L'ID abonnement est obligatoire")
    @Column(name = "id_abonnement")
    private UUID idAbonnement;

    @Column(name = "nom_abonnement")
    private String nomAbonnement;

    @Column(name = "id_facture_generee")
    private UUID idFactureGeneree;

    @Column(name = "numero_facture_generee")
    private String numeroFactureGeneree;

    @NotNull(message = "La date d'exécution est obligatoire")
    @Column(name = "date_execution")
    private LocalDateTime dateExecution;

    @Column(name = "date_facture")
    private LocalDateTime dateFacture;

    @Column(name = "montant_facture")
    private BigDecimal montantFacture;

    @Column(name = "succes")
    @Builder.Default
    private Boolean succes = false;

    @Column(name = "message_erreur")
    private String messageErreur;

    @Column(name = "details_execution")
    private String detailsExecution;

    @Column(name = "email_envoye")
    @Builder.Default
    private Boolean emailEnvoye = false;

    @Column(name = "pdf_genere")
    @Builder.Default
    private Boolean pdfGenere = false;

    @Column(name = "temps_execution_ms")
    private Long tempsExecutionMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}