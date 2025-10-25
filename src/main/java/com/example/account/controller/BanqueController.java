package com.example.account.controller;

import com.example.account.dto.request.BanqueCreateRequest;
import com.example.account.dto.request.BanqueUpdateRequest;
import com.example.account.dto.response.BanqueResponse;
import com.example.account.service.BanqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banques")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Banque", description = "API de gestion des banques")
public class BanqueController {

    private final BanqueService banqueService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle banque")
    public ResponseEntity<BanqueResponse> createBanque(@Valid @RequestBody BanqueCreateRequest request) {
        log.info("Requête de création de banque: {}", request.getBanque());
        BanqueResponse response = banqueService.createBanque(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{banqueId}")
    @Operation(summary = "Mettre à jour une banque")
    public ResponseEntity<BanqueResponse> updateBanque(
            @PathVariable UUID banqueId,
            @Valid @RequestBody BanqueUpdateRequest request) {
        log.info("Requête de mise à jour de la banque: {}", banqueId);
        BanqueResponse response = banqueService.updateBanque(banqueId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{banqueId}")
    @Operation(summary = "Récupérer une banque par ID")
    public ResponseEntity<BanqueResponse> getBanqueById(@PathVariable UUID banqueId) {
        log.info("Requête de récupération de la banque: {}", banqueId);
        BanqueResponse response = banqueService.getBanqueById(banqueId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero-compte/{numeroCompte}")
    @Operation(summary = "Récupérer une banque par numéro de compte")
    public ResponseEntity<BanqueResponse> getBanqueByNumeroCompte(@PathVariable String numeroCompte) {
        log.info("Requête de récupération de la banque par numéro de compte: {}", numeroCompte);
        BanqueResponse response = banqueService.getBanqueByNumeroCompte(numeroCompte);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les banques")
    public ResponseEntity<List<BanqueResponse>> getAllBanques() {
        log.info("Requête de récupération de toutes les banques");
        List<BanqueResponse> responses = banqueService.getAllBanques();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer toutes les banques avec pagination")
    public ResponseEntity<Page<BanqueResponse>> getAllBanquesPaginated(Pageable pageable) {
        log.info("Requête de récupération de toutes les banques avec pagination");
        Page<BanqueResponse> responses = banqueService.getAllBanques(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/nom/{nomBanque}")
    @Operation(summary = "Récupérer les banques par nom")
    public ResponseEntity<List<BanqueResponse>> getBanquesByName(@PathVariable String nomBanque) {
        log.info("Requête de récupération des banques par nom: {}", nomBanque);
        List<BanqueResponse> responses = banqueService.getBanquesByName(nomBanque);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/nom")
    @Operation(summary = "Rechercher des banques par nom")
    public ResponseEntity<List<BanqueResponse>> searchBanquesByName(@RequestParam String nom) {
        log.info("Requête de recherche des banques par nom: {}", nom);
        List<BanqueResponse> responses = banqueService.searchBanquesByName(nom);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/numero-compte")
    @Operation(summary = "Rechercher des banques par numéro de compte")
    public ResponseEntity<List<BanqueResponse>> searchBanquesByNumeroCompte(@RequestParam String numeroCompte) {
        log.info("Requête de recherche des banques par numéro de compte: {}", numeroCompte);
        List<BanqueResponse> responses = banqueService.searchBanquesByNumeroCompte(numeroCompte);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{banqueId}")
    @Operation(summary = "Supprimer une banque")
    public ResponseEntity<Void> deleteBanque(@PathVariable UUID banqueId) {
        log.info("Requête de suppression de la banque: {}", banqueId);
        banqueService.deleteBanque(banqueId);
        return ResponseEntity.noContent().build();
    }
}
