package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

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
    name = "alerte_tableau",
    indexes = {
        @Index(name = "idx_alertetableau_org", columnList = "organization_id")
    }
)
public class AlerteTableau extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_alerte")
    private UUID idAlerte;

    @Column(name = "nom_alerte")
    private String nomAlerte;

    @Column(name = "description")
    private String description;

    @Column(name = "id_widget")
    private UUID idWidget;

    @Column(name = "metrique_surveillee")
    private String metriqueSurveillee;

    @Column(name = "operateur")
    private String operateur; // "SUPERIEUR", "INFERIEUR", "EGAL", "DIFFERENT"

    @Column(name = "valeur_seuil")
    private BigDecimal valeurSeuil;

    @Column(name = "pourcentage_variation")
    private BigDecimal pourcentageVariation;

    @Column(name = "periode_comparaison")
    private String periodeComparaison; // "JOUR_PRECEDENT", "SEMAINE_PRECEDENTE", etc.

    @Column(name = "frequence_verification")
    private Integer frequenceVerification; // en minutes

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "niveau_criticite")
    private String niveauCriticite; // "INFO", "WARNING", "ERROR", "CRITICAL"

    // ✅ Element collections for simple value lists
    @ElementCollection
    @CollectionTable(
        name = "alerte_destinataires",
        joinColumns = @JoinColumn(name = "id_alerte")
    )
    @Column(name = "destinataire")
    private List<String> destinatairesNotification;

    @ElementCollection
    @CollectionTable(
        name = "alerte_canaux",
        joinColumns = @JoinColumn(name = "id_alerte")
    )
    @Column(name = "canal")
    private List<String> canalNotification; // "EMAIL", "SMS", "PUSH", "SLACK"

    @Column(name = "message_personnalise")
    private String messagePersonnalise;

    @ElementCollection
    @CollectionTable(
        name = "alerte_actions",
        joinColumns = @JoinColumn(name = "id_alerte")
    )
    @Column(name = "action")
    private List<String> actionsAutomatiques;

    @Column(name = "derniere_verification")
    private LocalDateTime derniereVerification;

    @Column(name = "derniere_alerte")
    private LocalDateTime derniereAlerte;

    @Column(name = "nombre_declenchements")
    @Builder.Default
    private Long nombreDeclenchements = 0L;

    @Column(name = "cooldown_minutes")
    @Builder.Default
    private Integer cooldownMinutes = 60; // éviter spam d'alertes

    @Column(name = "conditions_complementaires")
    private String conditionsComplementaires;
}
