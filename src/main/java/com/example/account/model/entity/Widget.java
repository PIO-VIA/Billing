package com.yooyob.erp.model.entity;

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

    @Column(name = "parametres_graphique")
    private Map<String, Object> parametresGraphique;

    @Column(name = "colonnes_affichees")
    private List<String> colonnesAffichees;

    @Column(name = "filtres_widget")
    private Map<String, Object> filtresWidget;

    @Column(name = "agregations")
    private Map<String, String> agregations; // "SUM", "AVG", "COUNT", etc.

    @Column(name = "groupement")
    private List<String> groupement;

    @Column(name = "tri")
    private Map<String, String> tri; // colonne -> "ASC"/"DESC"

    @Column(name = "limite_resultats")
    private Integer limiteResultats;

    @Column(name = "couleurs_personnalisees")
    private List<String> couleursPersonnalisees;

    @Column(name = "format_affichage")
    private Map<String, String> formatAffichage;

    @Column(name = "unite_mesure")
    private String uniteMesure;

    @Column(name = "precision_decimale")
    private Integer precisionDecimale;

    @Column(name = "seuils_alertes")
    private Map<String, Object> seuilsAlertes;

    @Column(name = "actions_clic")
    private Map<String, Object> actionsClic;

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