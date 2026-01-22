package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.response.BonCommandeResponse;
import com.example.account.modules.facturation.service.BonCommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bon commande")
    public ResponseEntity<Void> deleteBonCommande(@PathVariable UUID id) {
        bonCommandeService.deleteBonCommande(id);
        return ResponseEntity.noContent().build();
    }
}
