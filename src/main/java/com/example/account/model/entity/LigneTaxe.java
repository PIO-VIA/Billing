package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ligne_taxe")
public class LigneTaxe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ligne")
    private UUID idLigne;

    @NotNull(message = "Le type de ratio est obligatoire")
    @Column(name = "type_ratio")
    private String typeRatio;

    @NotNull(message = "Le ratio est obligatoire")
    @Column(name = "ratio")
    private BigDecimal ratio;

    @NotNull(message = "Le montant de base est obligatoire")
    @Column(name = "montant_base")
    private BigDecimal montantBase;

    @NotNull(message = "Le montant de la taxe est obligatoire")
    @Column(name = "montant_taxe")
    private BigDecimal montantTaxe;

    @Column(name = "nom_taxe")
    private String nomTaxe;

    @Column(name = "id_taxe")
    private UUID idTaxe;
}