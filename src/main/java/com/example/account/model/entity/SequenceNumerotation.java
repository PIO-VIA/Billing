package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeNumerotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sequences_numerotation")
public class SequenceNumerotation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_sequence")
    private UUID idSequence;

    @NotNull(message = "L'ID de configuration est obligatoire")
    @Column(name = "id_configuration")
    private UUID idConfiguration;

    @NotNull(message = "Le type de numérotation est obligatoire")
    @Column(name = "type_numerotation")
    private TypeNumerotation typeNumerotation;

    @Column(name = "cle_sequence")
    private String cleSequence; // ex: "FACTURE-2024" pour reset annuel

    @Column(name = "valeur_courante")
    @Builder.Default
    private Long valeurCourante = 0L;

    @Column(name = "valeur_precedente")
    private Long valeurPrecedente;

    @Column(name = "derniere_utilisation")
    private LocalDateTime derniereUtilisation;

    @Column(name = "derniere_valeur_generee")
    private String derniereValeurGeneree;

    @Column(name = "nombre_utilisations")
    @Builder.Default
    private Long nombreUtilisations = 0L;

    @Column(name = "verrouille")
    @Builder.Default
    private Boolean verrouille = false;

    @Column(name = "motif_verrouillage")
    private String motifVerrouillage;

    @Column(name = "date_reset")
    private LocalDateTime dateReset;

    @Column(name = "prochaine_date_reset")
    private LocalDateTime prochaineDateReset;

    @Column(name = "periode_courante")
    private String periodeCourante; // "2024", "2024-01", etc.

    @Column(name = "site")
    private String site;

    @Column(name = "departement")
    private String departement;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}