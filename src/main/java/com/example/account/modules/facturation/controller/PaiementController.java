package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.response.PaiementResponse;
import com.example.account.modules.facturation.service.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete paiement")
    public ResponseEntity<Void> deletePaiement(@PathVariable UUID id) {
        paiementService.deletePaiement(id);
        return ResponseEntity.noContent().build();
    }
}
