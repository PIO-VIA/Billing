package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("tableaux_bord")
public class TableauBord {

    @Id
    @Column("id_tableau")
    private UUID idTableau;

    @Column("nom_tableau")
    private String nomTableau;

    @Column("description")
    private String description;

    @Column("proprietaire")
    private UUID proprietaire;

    @Column("nom_proprietaire")
    private String nomProprietaire;

    @Column("type_tableau")
    private String typeTableau; // "EXECUTIF", "COMMERCIAL", "FINANCIER", "OPERATIONNEL"

    @Column("widgets")
    private String widgetsJson;

    @Column("layout_configuration")
    private String layoutConfigurationJson;

    @Column("filtres_globaux")
    private String filtresGlobauxJson;

    @Column("periode_defaut")
    private String periodeDefaut; // "MOIS_COURANT", "TRIMESTRE", "ANNEE", etc.

    @Column("auto_actualisation")
    @Builder.Default
    private Boolean autoActualisation = true;

    @Column("frequence_actualisation_minutes")
    @Builder.Default
    private Integer frequenceActualisationMinutes = 15;

    @Column("partage_public")
    @Builder.Default
    private Boolean partagePublic = false;

    @Column("utilisateurs_autorises")
    private String utilisateursAutorisesJson;

    @Column("roles_autorises")
    private String rolesAutorisesJson;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("par_defaut")
    @Builder.Default
    private Boolean parDefaut = false;

    @Column("ordre_affichage")
    @Builder.Default
    private Integer ordreAffichage = 1;

    @Column("couleur_theme")
    private String couleurTheme;

    @Column("icone")
    private String icone;

    @Column("tags")
    private String tagsJson;

    @Column("derniere_consultation")
    private LocalDateTime derniereConsultation;

    @Column("nombre_consultations")
    @Builder.Default
    private Long nombreConsultations = 0L;

    @Column("favoris_utilisateurs")
    private String favorisUtilisateursJson;

    @Column("export_automatique")
    @Builder.Default
    private Boolean exportAutomatique = false;

    @Column("format_export")
    private String formatExport; // "PDF", "EXCEL", "EMAIL"

    @Column("destinataires_export")
    private String destinatairesExportJson;

    @Column("frequence_export")
    private String frequenceExport; // "QUOTIDIEN", "HEBDOMADAIRE", "MENSUEL"

    @Column("derniere_actualisation")
    private LocalDateTime derniereActualisation;

    @Column("cache_donnees")
    private String cacheDonneesJson;

    @Column("duree_cache_minutes")
    @Builder.Default
    private Integer dureeCacheMinutes = 30;

    @Column("alertes_configurees")
    private String alertesConfigureesJson;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}