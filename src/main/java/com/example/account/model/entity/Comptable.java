package com.example.account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comptables")
public class Comptable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_comptable")
    private UUID idComptable;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column(name = "username")
    private String username;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
