package com.example.account.model.entity;

import com.example.account
.model.enums.StatutApprobation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "historique_approbation")
public class HistoriqueApprobation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historique")
    private UUID idHistorique;

    @Column(name = "ordre_action")
    private Integer ordreAction;

    @Column(name = "etape")
    private Integer etape;

    @Column(name = "nom_etape")
    private String nomEtape;

    @Column(name = "approbateur")
    private UUID approbateur;

    @Column(name = "nom_approbateur")
    private String nomApprobateur;

    @Column(name = "action")
    private StatutApprobation action;

    @Column(name = "commentaires")
    private String commentaires;

    @Column(name = "date_action")
    private LocalDateTime dateAction;

    @Column(name = "delai_reponse_heures")
    private Long delaiReponseHeures;

    @Column(name = "escalade")
    @Builder.Default
    private Boolean escalade = false;

    @Column(name = "escalade_depuis")
    private UUID escaladeDepuis;

    @Column(name = "ip_adresse")
    private String ipAdresse;

    @Column(name = "metadata")
    private String metadata;
}