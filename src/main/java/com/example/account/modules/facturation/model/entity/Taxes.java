package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("taxes")
public class Taxes extends OrganizationScoped {

    @Id
    @Column("id_taxe")
    private UUID idTaxe;

    @NotBlank(message = "Le nom de la taxe est obligatoire")
    @Column("nom_taxe")
    private String nomTaxe;

    @NotNull(message = "Le calcul de la taxe est obligatoire")
    @PositiveOrZero(message = "Le calcul de la taxe doit être positif ou nul")
    @Column("calcul_taxe")
    private BigDecimal calculTaxe;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @NotBlank(message = "Le type de taxe est obligatoire")
    @Column("type_taxe")
    private String typeTaxe;

    @Column("porte_taxe")
    private String porteTaxe;

    @NotNull(message = "Le montant est obligatoire")
    @Column("montant")
    private BigDecimal montant;

    @Column("position_fiscale")
    private String positionFiscale;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
