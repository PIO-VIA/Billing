package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tableaux_bord")
public class TableauBord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tableau")
    private UUID idTableau;

    @NotBlank(message = "Le nom du tableau de bord est obligatoire")
    @Column(name = "nom_tableau")
    private String nomTableau;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Le propriétaire est obligatoire")
    @Column(name = "proprietaire")
    private UUID proprietaire;

    @Column(name = "nom_proprietaire")
    private String nomProprietaire;

    @Column(name = "type_tableau")
    private String typeTableau; // "EXECUTIF", "COMMERCIAL", "FINANCIER", "OPERATIONNEL"

    @Column(name = "widgets")
    private List<Widget> widgets;

    @Column(name = "layout_configuration")
    private Map<String, Object> layoutConfiguration;

    @Column(name = "filtres_globaux")
    private Map<String, Object> filtresGlobaux;

    @Column(name = "periode_defaut")
    private String periodeDefaut; // "MOIS_COURANT", "TRIMESTRE", "ANNEE", etc.

    @Column(name = "auto_actualisation")
    @Builder.Default
    private Boolean autoActualisation = true;

    @Column(name = "frequence_actualisation_minutes")
    @Builder.Default
    private Integer frequenceActualisationMinutes = 15;

    @Column(name = "partage_public")
    @Builder.Default
    private Boolean partagePublic = false;

    @Column(name = "utilisateurs_autorises")
    private List<UUID> utilisateursAutorises;

    @Column(name = "roles_autorises")
    private List<String> rolesAutorises;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "par_defaut")
    @Builder.Default
    private Boolean parDefaut = false;

    @Column(name = "ordre_affichage")
    @Builder.Default
    private Integer ordreAffichage = 1;

    @Column(name = "couleur_theme")
    private String couleurTheme;

    @Column(name = "icone")
    private String icone;

    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "derniere_consultation")
    private LocalDateTime derniereConsultation;

    @Column(name = "nombre_consultations")
    @Builder.Default
    private Long nombreConsultations = 0L;

    @Column(name = "favoris_utilisateurs")
    private List<UUID> favorisUtilisateurs;

    @Column(name = "export_automatique")
    @Builder.Default
    private Boolean exportAutomatique = false;

    @Column(name = "format_export")
    private String formatExport; // "PDF", "EXCEL", "EMAIL"

    @Column(name = "destinataires_export")
    private List<String> destinatairesExport;

    @Column(name = "frequence_export")
    private String frequenceExport; // "QUOTIDIEN", "HEBDOMADAIRE", "MENSUEL"

    @Column(name = "derniere_actualisation")
    private LocalDateTime derniereActualisation;

    @Column(name = "cache_donnees")
    private Map<String, Object> cacheDonnees;

    @Column(name = "duree_cache_minutes")
    @Builder.Default
    private Integer dureeCacheMinutes = 30;

    @Column(name = "alertes_configurees")
    private List<AlerteTableau> alertesConfigurees;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}