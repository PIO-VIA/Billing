package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "banques")
public class Banque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_banque")
    private UUID idBanque;

    @NotBlank(message = "Le numéro de compte est obligatoire")
    @Column(name = "numero_compte")
    private String numeroCompte;

    @NotBlank(message = "Le nom de la banque est obligatoire")
    @Column(name = "banque")
    private String banque;
}
