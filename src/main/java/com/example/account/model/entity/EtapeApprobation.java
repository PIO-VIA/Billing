package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "etape_approbation")
public class EtapeApprobation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_etape")
    private UUID idEtape;

    @Column(name = "ordre_etape")
    private Integer ordreEtape;

    @Column(name = "nom_etape")
    private String nomEtape;

    @Column(name = "description")
    private String description;

    @Column(name = "approbateurs_requis", columnDefinition = "TEXT")
    private String approubateursRequisJson;

    @Column(name = "roles_approbateurs", columnDefinition = "TEXT")
    private String rolesApprobateursJson;

    @Column(name = "nombre_approbations_requises")
    @Builder.Default
    private Integer nombreApprobationsRequises = 1;

    @Column(name = "obligatoire")
    @Builder.Default
    private Boolean obligatoire = true;

    @Column(name = "parallele")
    @Builder.Default
    private Boolean parallele = false;

    @Column(name = "conditions_passage")
    private String conditionsPassage;

    @Column(name = "delai_max_heures")
    private Integer delaiMaxHeures;

    @Column(name = "escalade_vers", columnDefinition = "TEXT")
    private String escaladeVersJson;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;
}