package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.BonAchatRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.service.BonAchatService;
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
@RequestMapping("/api/bons-achat")
@RequiredArgsConstructor
@Tag(name = "Bon d'achat", description = "API de gestion des Bons d'achat (Goods Receipt Notes)")
public class BonAchatController {

    private final BonAchatService bonAchatService;

    @PostMapping
    @Operation(summary = "Créer un nouveau bon d'achat")
    public ResponseEntity<BonAchatResponse> createBonAchat(@Valid @RequestBody BonAchatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bonAchatService.createBonAchat(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un bon d'achat par ID")
    public ResponseEntity<BonAchatResponse> getBonAchatById(@PathVariable UUID id) {
        return ResponseEntity.ok(bonAchatService.getBonAchatById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les bons d'achat")
    public ResponseEntity<List<BonAchatResponse>> getAllBonsAchat() {
        return ResponseEntity.ok(bonAchatService.getAllBonsAchat());
    }

    @GetMapping("/fournisseur/{idFournisseur}")
    @Operation(summary = "Lister les bons d'achat par fournisseur")
    public ResponseEntity<List<BonAchatResponse>> getBonsAchatByFournisseur(@PathVariable UUID idFournisseur) {
        return ResponseEntity.ok(bonAchatService.getBonsAchatByFournisseur(idFournisseur));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bon d'achat")
    public ResponseEntity<Void> deleteBonAchat(@PathVariable UUID id) {
        bonAchatService.deleteBonAchat(id);
        return ResponseEntity.noContent().build();
    }
}
