package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.PaiementCreateRequest;
import com.example.account.modules.facturation.dto.request.PaiementUpdateRequest;
import com.example.account.modules.facturation.dto.response.PaiementResponse;
import com.example.account.modules.facturation.service.PaiementService;
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
@RequestMapping("/api/paiement")
@RequiredArgsConstructor
@Tag(name = "Paiement")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement")
    public ResponseEntity<PaiementResponse> createPaiement(@Valid @RequestBody PaiementCreateRequest request) {
        PaiementResponse response = paiementService.createPaiement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un paiement")
    public ResponseEntity<PaiementResponse> updatePaiement(
            @PathVariable UUID id,
            @Valid @RequestBody PaiementUpdateRequest request) {
        PaiementResponse response = paiementService.updatePaiement(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all paiements")
    public ResponseEntity<List<PaiementResponse>> getAllPaiements() {
        return ResponseEntity.ok(paiementService.getAllPaiements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get paiement by ID")
    public ResponseEntity<PaiementResponse> getPaiementById(@PathVariable UUID id) {
        return ResponseEntity.ok(paiementService.getPaiementById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les paiements d'un client")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(paiementService.getPaiementsByClient(clientId));
    }

    @GetMapping("/facture/{factureId}")
    @Operation(summary = "Récupérer les paiements d'une facture")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByFacture(@PathVariable UUID factureId) {
        return ResponseEntity.ok(paiementService.getPaiementsByFacture(factureId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete paiement")
    public ResponseEntity<Void> deletePaiement(@PathVariable UUID id) {
        paiementService.deletePaiement(id);
        return ResponseEntity.noContent().build();
    }
}
