package com.example.account.controller;

import com.example.account.dto.request.FournisseurCreateRequest;
import com.example.account.dto.request.FournisseurUpdateRequest;
import com.example.account.dto.response.FournisseurResponse;
import com.example.account.service.FournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fournisseur", description = "API de gestion des fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @PostMapping
    @Operation(summary = "Créer un nouveau fournisseur")
    public ResponseEntity<FournisseurResponse> createFournisseur(@Valid @RequestBody FournisseurCreateRequest request) {
        log.info("Requête de création de fournisseur: {}", request.getUsername());
        FournisseurResponse response = fournisseurService.createFournisseur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{fournisseurId}")
    @Operation(summary = "Mettre à jour un fournisseur")
    public ResponseEntity<FournisseurResponse> updateFournisseur(
            @PathVariable UUID fournisseurId,
            @Valid @RequestBody FournisseurUpdateRequest request) {
        log.info("Requête de mise à jour du fournisseur: {}", fournisseurId);
        FournisseurResponse response = fournisseurService.updateFournisseur(fournisseurId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fournisseurId}")
    @Operation(summary = "Récupérer un fournisseur par ID")
    public ResponseEntity<FournisseurResponse> getFournisseurById(@PathVariable UUID fournisseurId) {
        log.info("Requête de récupération du fournisseur: {}", fournisseurId);
        FournisseurResponse response = fournisseurService.getFournisseurById(fournisseurId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Récupérer un fournisseur par username")
    public ResponseEntity<FournisseurResponse> getFournisseurByUsername(@PathVariable String username) {
        log.info("Requête de récupération du fournisseur par username: {}", username);
        FournisseurResponse response = fournisseurService.getFournisseurByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les fournisseurs")
    public ResponseEntity<List<FournisseurResponse>> getAllFournisseurs() {
        log.info("Requête de récupération de tous les fournisseurs");
        List<FournisseurResponse> responses = fournisseurService.getAllFournisseurs();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/actifs")
    @Operation(summary = "Récupérer tous les fournisseurs actifs")
    public ResponseEntity<List<FournisseurResponse>> getActiveFournisseurs() {
        log.info("Requête de récupération des fournisseurs actifs");
        List<FournisseurResponse> responses = fournisseurService.getActiveFournisseurs();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Récupérer les fournisseurs par catégorie")
    public ResponseEntity<List<FournisseurResponse>> getFournisseursByCategorie(@PathVariable String categorie) {
        log.info("Requête de récupération des fournisseurs par catégorie: {}", categorie);
        List<FournisseurResponse> responses = fournisseurService.getFournisseursByCategorie(categorie);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{fournisseurId}")
    @Operation(summary = "Supprimer un fournisseur")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable UUID fournisseurId) {
        log.info("Requête de suppression du fournisseur: {}", fournisseurId);
        fournisseurService.deleteFournisseur(fournisseurId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{fournisseurId}/solde")
    @Operation(summary = "Mettre à jour le solde d'un fournisseur")
    public ResponseEntity<FournisseurResponse> updateSolde(
            @PathVariable UUID fournisseurId,
            @RequestParam Double montant) {
        log.info("Requête de mise à jour du solde du fournisseur {}: {}", fournisseurId, montant);
        FournisseurResponse response = fournisseurService.updateSolde(fournisseurId, montant);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fournisseurId}/desactiver")
    @Operation(summary = "Désactiver un fournisseur")
    public ResponseEntity<FournisseurResponse> desactiverFournisseur(@PathVariable UUID fournisseurId) {
        log.info("Requête de désactivation du fournisseur: {}", fournisseurId);
        FournisseurResponse response = fournisseurService.desactiverFournisseur(fournisseurId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fournisseurId}/activer")
    @Operation(summary = "Activer un fournisseur")
    public ResponseEntity<FournisseurResponse> activerFournisseur(@PathVariable UUID fournisseurId) {
        log.info("Requête d'activation du fournisseur: {}", fournisseurId);
        FournisseurResponse response = fournisseurService.activerFournisseur(fournisseurId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/actifs")
    @Operation(summary = "Compter les fournisseurs actifs")
    public ResponseEntity<Long> countActiveFournisseurs() {
        log.info("Requête de comptage des fournisseurs actifs");
        Long count = fournisseurService.countActiveFournisseurs();
        return ResponseEntity.ok(count);
    }
}
