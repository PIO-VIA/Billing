package com.example.account
.dto.request;

import com.example.account
.model.enums.TypeEscompte;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationEscompteCreateRequest {

    @NotNull(message = "Le nom de la configuration est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit faire entre 3 et 100 caractères")
    private String nomConfiguration;

    @NotNull(message = "Le type d'escompte est obligatoire")
    private TypeEscompte typeEscompte;

    @DecimalMin(value = "0.0", message = "Le taux d'escompte doit être positif")
    @DecimalMax(value = "100.0", message = "Le taux d'escompte ne peut pas dépasser 100%")
    private BigDecimal tauxEscompte;

    @DecimalMin(value = "0.0", message = "Le montant fixe doit être positif")
    private BigDecimal montantFixeEscompte;

    private Integer nombreJoursAvance;

    @DecimalMin(value = "0.0", message = "Le montant minimal doit être positif")
    private BigDecimal montantMinimal;

    private BigDecimal montantMaximal;

    private LocalDate dateDebutValidite;

    private LocalDate dateFinValidite;

    @Builder.Default
    private Boolean automatique = false;

    @Builder.Default
    private Boolean cumulable = false;

    @Builder.Default
    private Boolean actif = true;

    private String description;

    private String conditionsSpeciales;

    private Integer priorite;
}