package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mesures")
public class Mesure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_mesure")
    private UUID idMesure;

    @NotBlank(message = "Le nom de la mesure est obligatoire")
    @Column(name = "nom_mesure")
    private String nomMesure;

    @NotNull(message = "Le facteur de conversion est obligatoire")
    @Positive(message = "Le facteur de conversion doit être positif")
    @Column(name = "facteur_conversion")
    private BigDecimal facteurConversion;

    @Column(name = "unite_base")
    private String uniteBase;

    @Column(name = "symbole")
    private String symbole;

    @Column(name = "type_mesure")
    private String typeMesure;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}