package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.BonLivraisonRequest;
import com.example.account.modules.facturation.dto.response.BonLivraisonResponse;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import com.example.account.modules.facturation.service.BonLivraisonService;
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
@RequestMapping("/api/bons-livraison")
@RequiredArgsConstructor
@Tag(name = "Bon de livraison", description = "API de gestion des Bons de livraison (Delivery Notes)")
public class BonLivraisonController {

    private final BonLivraisonService bonLivraisonService;

    @PostMapping
    @Operation(summary = "Créer un nouveau bon de livraison")
    public ResponseEntity<BonLivraisonResponse> createBonLivraison(@Valid @RequestBody BonLivraisonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bonLivraisonService.createBonLivraison(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un bon de livraison par ID")
    public ResponseEntity<BonLivraisonResponse> getBonLivraisonById(@PathVariable UUID id) {
        return ResponseEntity.ok(bonLivraisonService.getBonLivraisonById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les bons de livraison")
    public ResponseEntity<List<BonLivraisonResponse>> getAllBonLivraisons() {
        return ResponseEntity.ok(bonLivraisonService.getAllBonLivraisons());
    }

    @GetMapping("/client/{idClient}")
    @Operation(summary = "Lister les bons de livraison par client")
    public ResponseEntity<List<BonLivraisonResponse>> getBonLivraisonsByClient(@PathVariable UUID idClient) {
        return ResponseEntity.ok(bonLivraisonService.getBonLivraisonsByClient(idClient));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'un bon de livraison")
    public ResponseEntity<BonLivraisonResponse> updateStatut(
            @PathVariable UUID id,
            @RequestParam StatutBonLivraison statut) {
        return ResponseEntity.ok(bonLivraisonService.updateStatut(id, statut));
    }

    @PostMapping("/{id}/effectuer")
    @Operation(summary = "Marquer une livraison comme effectuée")
    public ResponseEntity<BonLivraisonResponse> marquerCommeEffectuee(@PathVariable UUID id) {
        return ResponseEntity.ok(bonLivraisonService.marquerCommeEffectuee(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bon de livraison")
    public ResponseEntity<Void> deleteBonLivraison(@PathVariable UUID id) {
        bonLivraisonService.deleteBonLivraison(id);
        return ResponseEntity.noContent().build();
    }
}
