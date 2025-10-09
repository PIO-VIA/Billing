package com.example.account
.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueTauxChangeCreateRequest {

    @NotNull(message = "La devise source est obligatoire")
    @Size(min = 3, max = 3, message = "Le code devise source doit faire 3 caractères")
    private String deviseSource;

    @NotNull(message = "La devise cible est obligatoire")
    @Size(min = 3, max = 3, message = "Le code devise cible doit faire 3 caractères")
    private String deviseCible;

    @NotNull(message = "Le taux de change est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le taux de change doit être positif")
    private BigDecimal tauxChange;

    @NotNull(message = "La date d'application est obligatoire")
    private LocalDateTime dateApplication;

    private String sourceTaux;

    @Builder.Default
    private Boolean automatique = false;

    @Builder.Default
    private Boolean actif = true;

    private String description;

    private BigDecimal commissionPourcentage;

    private BigDecimal commissionFixe;

    private BigDecimal seuilAlerte;

    private String fournisseurApi;

    private String referenceTaux;
}