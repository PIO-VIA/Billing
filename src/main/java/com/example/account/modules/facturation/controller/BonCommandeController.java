package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.BonCommandeCreateRequest;
import com.example.account.modules.facturation.dto.response.BonCommandeResponse;
import com.example.account.modules.facturation.model.enums.StatusBonCommande;
import com.example.account.modules.facturation.service.BonCommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bon-commande")
@RequiredArgsConstructor
@Tag(name = "Bon Commande")
public class BonCommandeController {

    private final BonCommandeService bonCommandeService;

    @PostMapping
    @Operation(summary = "Créer un bon de commande")
    public ResponseEntity<BonCommandeResponse> createBonCommande(@Valid @RequestBody BonCommandeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bonCommandeService.createBonCommande(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update bon commande by ID")
    public ResponseEntity<BonCommandeResponse> updateBonCommandeById(@PathVariable UUID id,@RequestBody BonCommandeCreateRequest request) {
        return ResponseEntity.ok(bonCommandeService.updateBonCommande(id, request));
    }
    @GetMapping
    @Operation(summary = "Get all bons de commande")
    public ResponseEntity<List<BonCommandeResponse>> getAllBonCommandes() {
        return ResponseEntity.ok(bonCommandeService.getAllBonCommandes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bon commande by ID")
    public ResponseEntity<BonCommandeResponse> getBonCommandeById(@PathVariable UUID id) {
        return ResponseEntity.ok(bonCommandeService.getBonCommandeById(id));
    }

    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Changer l'état d'un bon de commande")
    public ResponseEntity<BonCommandeResponse> updateStatut(
            @PathVariable UUID id,
            @RequestParam StatusBonCommande statut) {
        return ResponseEntity.ok(bonCommandeService.updateStatut(id, statut));
    }

}
