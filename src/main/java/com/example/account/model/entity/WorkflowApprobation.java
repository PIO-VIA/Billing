package com.example.account.model.entity;

import com.example.account.model.enums.TypeWorkflow;
import com.example.account.model.enums.StatutApprobation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workflows_approbation")
public class WorkflowApprobation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_workflow")
    private UUID idWorkflow;

    @NotNull(message = "Le type de workflow est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_workflow")
    private TypeWorkflow typeWorkflow;

    @Column(name = "nom_workflow")
    private String nomWorkflow;

    @Column(name = "description")
    private String description;

    @Column(name = "montant_seuil_min")
    private BigDecimal montantSeuilMin;

    @Column(name = "montant_seuil_max")
    private BigDecimal montantSeuilMax;

    // ✅ FIXED: Define relationship properly
    @OneToMany(mappedBy = "idEtape", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtapeApprobation> etapesApprobation;

    @Column(name = "conditions_declenchement")
    private String conditionsDeclenchement;

    @Column(name = "auto_approuver_si_montant_inferieur")
    private BigDecimal autoApprouverSiMontantInferieur;

    @Column(name = "delai_expiration_heures")
    @Builder.Default
    private Integer delaiExpirationHeures = 72;

    @Column(name = "escalade_automatique")
    @Builder.Default
    private Boolean escaladeAutomatique = true;

    @Column(name = "delai_escalade_heures")
    @Builder.Default
    private Integer delaiEscaladeHeures = 24;

    @Column(name = "notification_email")
    @Builder.Default
    private Boolean notificationEmail = true;

    @Column(name = "template_email")
    private String templateEmail;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "ordre_priorite")
    @Builder.Default
    private Integer ordrePriorite = 1;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
