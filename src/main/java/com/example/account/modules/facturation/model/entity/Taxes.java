package com.example.account.modules.facturation.model.entity;

import com.example.account.modules.core.model.entity.OrganizationScoped;
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
@Table(
    name = "taxes",
    indexes = {
        @Index(name = "idx_taxes_org", columnList = "organization_id"),
        @Index(name = "idx_taxes_org_nomtaxe", columnList = "organization_id, nom_taxe")
    }
)
public class Taxes extends OrganizationScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_taxe")
    private UUID idTaxe;

    @NotBlank(message = "Le nom de la taxe est obligatoire")
    @Column(name = "nom_taxe")
    private String nomTaxe;

    @NotNull(message = "Le calcul de la taxe est obligatoire")
    @PositiveOrZero(message = "Le calcul de la taxe doit être positif ou nul")
    @Column(name = "calcul_taxe")
    private BigDecimal calculTaxe;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @NotBlank(message = "Le type de taxe est obligatoire")
    @Column(name = "type_taxe")
    private String typeTaxe;

    @Column(name = "porte_taxe")
    private String porteTaxe;

    @NotNull(message = "Le montant est obligatoire")
    @Column(name = "montant")
    private BigDecimal montant;

    @Column(name = "position_fiscale")
    private String positionFiscale;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
