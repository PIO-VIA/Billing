package com.example.account.model.entity;

import com.yooyob.erp.model.enums.TypeRelance;
import com.yooyob.erp.model.enums.StatutRelance;
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
@Table(name = "planifications_relance")
public class PlanificationRelance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_planification")
    private UUID idPlanification;

    @NotNull(message = "L'ID de configuration est obligatoire")
    @Column(name = "id_configuration")
    private UUID idConfiguration;

    @Column(name = "nom_configuration")
    private String nomConfiguration;

    @NotNull(message = "Le type de relance est obligatoire")
    @Column(name = "type_relance")
    private TypeRelance typeRelance;

    @NotNull(message = "L'ID de la facture est obligatoire")
    @Column(name = "id_facture")
    private UUID idFacture;

    @Column(name = "numero_facture")
    private String numeroFacture;

    @Column(name = "id_client")
    private UUID idClient;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "email_client")
    private String emailClient;

    @Column(name = "telephone_client")
    private String telephoneClient;

    @Column(name = "montant_facture")
    private BigDecimal montantFacture;

    @Column(name = "montant_restant")
    private BigDecimal montantRestant;

    @Column(name = "date_echeance")
    private LocalDateTime dateEcheance;

    @Column(name = "jours_retard")
    private Integer joursRetard;

    @Column(name = "statut")
    private StatutRelance statut;

    @Column(name = "date_planifiee")
    private LocalDateTime datePlanifiee;

    @Column(name = "date_envoi_prevue")
    private LocalDateTime dateEnvoiPrevue;

    @Column(name = "date_envoi_reelle")
    private LocalDateTime dateEnvoiReelle;

    @Column(name = "date_prochaine_tentative")
    private LocalDateTime dateProchaineTentative;

    @Column(name = "numero_tentative")
    @Builder.Default
    private Integer numeroTentative = 1;

    @Column(name = "numero_relance_sequence")
    @Builder.Default
    private Integer numeroRelanceSequence = 1;

    @Column(name = "contenu_email")
    private String contenuEmail;

    @Column(name = "objet_email")
    private String objetEmail;

    @Column(name = "destinataires_email")
    private List<String> destinatairesEmail;

    @Column(name = "copie_carbone")
    private List<String> copieCarbone;

    @Column(name = "pieces_jointes")
    private List<String> piecesJointes;

    @Column(name = "canal_envoi")
    private List<String> canalEnvoi; // EMAIL, SMS, COURRIER

    @Column(name = "reponse_recue")
    @Builder.Default
    private Boolean reponseRecue = false;

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @Column(name = "contenu_reponse")
    private String contenuReponse;

    @Column(name = "erreurs_envoi")
    private List<String> erreursEnvoi;

    @Column(name = "nombre_echecs")
    @Builder.Default
    private Integer nombreEchecs = 0;

    @Column(name = "logs_execution")
    private List<String> logsExecution;

    @Column(name = "metadata")
    private Map<String, String> metadata;

    @Column(name = "frais_appliques")
    private BigDecimal fraisAppliques;

    @Column(name = "escalade_effectuee")
    @Builder.Default
    private Boolean escaladeEffectuee = false;

    @Column(name = "date_escalade")
    private LocalDateTime dateEscalade;

    @Column(name = "escalade_vers")
    private List<String> escaladeVers;

    @Column(name = "annulee_automatiquement")
    @Builder.Default
    private Boolean annuleeAutomatiquement = false;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}