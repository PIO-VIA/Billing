package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeWorkflow;
import com.yooyob.erp.model.enums.StatutApprobation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "demandes_approbation")
public class DemandeApprobation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_demande")
    private UUID idDemande;

    @NotNull(message = "L'ID du workflow est obligatoire")
    @Column(name = "id_workflow")
    private UUID idWorkflow;

    @NotNull(message = "Le type de workflow est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_workflow")
    private TypeWorkflow typeWorkflow;

    @Column(name = "titre_demande")
    private String titreDemande;

    @Column(name = "description")
    private String description;

    @NotNull(message = "L'ID de l'objet est obligatoire")
    @Column(name = "id_objet")
    private UUID idObjet;

    @Column(name = "type_objet")
    private String typeObjet; // "FACTURE", "DEVIS", "AVOIR", etc.

    @Column(name = "donnees_objet")
    private Map<String, Object> donneesObjet;

    @Column(name = "montant_concerne")
    private BigDecimal montantConcerne;

    @NotNull(message = "Le demandeur est obligatoire")
    @Column(name = "demandeur")
    private UUID demandeur;

    @Column(name = "nom_demandeur")
    private String nomDemandeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutApprobation statut;

    @Column(name = "etape_courante")
    private Integer etapeCourante;

    @Column(name = "historique_approbations")
    private List<HistoriqueApprobation> historiqueApprobations;

    @Column(name = "approbateurs_en_attente")
    private List<UUID> approbateursEnAttente;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "date_derniere_action")
    private LocalDateTime dateDerniereAction;

    @Column(name = "date_finalisation")
    private LocalDateTime dateFinalisation;

    @Column(name = "commentaires_demandeur")
    private String commentairesDemandeur;

    @Column(name = "commentaires_finaux")
    private String commentairesFinaux;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "notifications_envoyees")
    private List<String> notificationsEnvoyees;

    @Column(name = "escalades_effectuees")
    @Builder.Default
    private Integer escaladeesEffectuees = 0;

    @Column(name = "priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "metadata")
    private Map<String, String> metadata;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}