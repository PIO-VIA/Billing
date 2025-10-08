package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeClient;
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
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_client")
    private UUID idClient;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column(name = "username")
    private String username;

    @NotBlank(message = "La catégorie est obligatoire")
    @Column(name = "categorie")
    private String categorie;

    @Column(name = "site_web")
    private String siteWeb;

    @Column(name = "n_tva")
    @Builder.Default
    private Boolean nTva = false;

    @NotBlank(message = "L'adresse est obligatoire")
    @Column(name = "adresse")
    private String adresse;

    @Column(name = "telephone")
    private String telephone;

    @Email(message = "Format d'email invalide")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Le type de client est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_client")
    private TypeClient typeClient;

    @Column(name = "raison_sociale")
    private String raisonSociale;

    @Column(name = "numero_tva")
    private String numeroTva;

    @Column(name = "code_client")
    private String codeClient;

    @Column(name = "limite_credit")
    private Double limiteCredit;

    @Column(name = "solde_courant")
    @Builder.Default
    private Double soldeCourant = 0.0;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}