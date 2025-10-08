package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "positions_fiscales")
public class PositionFiscale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_position_fiscale")
    private UUID idPositionFiscale;

    @NotNull(message = "Le statut par défaut est obligatoire")
    @Column(name = "defaut_auto")
    @Builder.Default
    private Boolean defautAuto = false;

    @NotBlank(message = "Le pays est obligatoire")
    @Column(name = "pays")
    private String pays;

    @Column(name = "n_identification_etranger")
    private String nIdentificationEtranger;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "description")
    private String description;

    @Column(name = "code_position")
    private String codePosition;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}