package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "widget")
public class Widget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_widget")
    private UUID idWidget;

    @Column(name = "nom_widget")
    private String nomWidget;

    @Column(name = "type_widget")
    private String typeWidget; // "KPI", "GRAPHIQUE", "TABLEAU", "JAUGE", "CALENDRIER"

    @Column(name = "sous_type")
    private String sousType; // "BAR", "LINE", "PIE", "DONUT", "AREA", etc.

    @Column(name = "titre")
    private String titre;

    @Column(name = "description")
    private String description;

    @Column(name = "position_x")
    private Integer positionX;

    @Column(name = "position_y")
    private Integer positionY;

    @Column(name = "largeur")
    private Integer largeur;

    @Column(name = "hauteur")
    private Integer hauteur;

    @Column(name = "source_donnees")
    private String sourceDonnees; // "FACTURES", "PAIEMENTS", "CLIENTS", etc.

    @Column(name = "requete_sql")
    private String requeteSql;

    @Column(name = "parametres_graphique", columnDefinition = "TEXT")
    private String parametresGraphiqueJson;

    @Column(name = "colonnes_affichees", columnDefinition = "TEXT")
    private String colonnesAfficheesJson;

    @Column(name = "filtres_widget", columnDefinition = "TEXT")
    private String filtresWidgetJson;

    @Column(name = "agregations", columnDefinition = "TEXT")
    private String agregationsJson; // "SUM", "AVG", "COUNT", etc.

    @Column(name = "groupement", columnDefinition = "TEXT")
    private String groupementJson;

    @Column(name = "tri", columnDefinition = "TEXT")
    private String triJson; // colonne -> "ASC"/"DESC"

    @Column(name = "limite_resultats")
    private Integer limiteResultats;

    @Column(name = "couleurs_personnalisees", columnDefinition = "TEXT")
    private String couleursPersonnaliseesJson;

    @Column(name = "format_affichage", columnDefinition = "TEXT")
    private String formatAffichageJson;

    @Column(name = "unite_mesure")
    private String uniteMesure;

    @Column(name = "precision_decimale")
    private Integer precisionDecimale;

    @Column(name = "seuils_alertes", columnDefinition = "TEXT")
    private String seuilsAlertesJson;

    @Column(name = "actions_clic", columnDefinition = "TEXT")
    private String actionsClicJson;

    @Column(name = "visible")
    @Builder.Default
    private Boolean visible = true;

    @Column(name = "actualisation_auto")
    @Builder.Default
    private Boolean actualisationAuto = true;

    @Column(name = "cache_active")
    @Builder.Default
    private Boolean cacheActive = true;

    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;

    @Column(name = "responsif")
    @Builder.Default
    private Boolean responsif = true;

    @Column(name = "exportable")
    @Builder.Default
    private Boolean exportable = true;

    @Column(name = "interactif")
    @Builder.Default
    private Boolean interactif = true;

    @Column(name = "donnees_temps_reel")
    @Builder.Default
    private Boolean donneesTempsReel = false;

    @Column(name = "websocket_channel")
    private String websocketChannel;
}