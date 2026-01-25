package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.ProformaInvoiceRequest;
import com.example.account.modules.facturation.dto.response.ProformaInvoiceResponse;
import com.example.account.modules.facturation.model.enums.StatutProforma;
import com.example.account.modules.facturation.service.FactureProformaService;
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
@RequestMapping("/api/factures-proforma")
@RequiredArgsConstructor
@Tag(name = "Factures Proforma", description = "API de gestion des Factures Proforma")
public class FactureProformaController {

    private final FactureProformaService proformaService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle facture proforma")
    public ResponseEntity<ProformaInvoiceResponse> createProforma(@Valid @RequestBody ProformaInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proformaService.createProforma(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une facture proforma par ID")
    public ResponseEntity<ProformaInvoiceResponse> getProformaById(@PathVariable UUID id) {
        return ResponseEntity.ok(proformaService.getProformaById(id));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les factures proforma")
    public ResponseEntity<List<ProformaInvoiceResponse>> getAllProformas() {
        return ResponseEntity.ok(proformaService.getAllProformas());
    }

    @GetMapping("/client/{idClient}")
    @Operation(summary = "Lister les factures proforma par client")
    public ResponseEntity<List<ProformaInvoiceResponse>> getProformasByClient(@PathVariable UUID idClient) {
        return ResponseEntity.ok(proformaService.getProformasByClient(idClient));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'une facture proforma")
    public ResponseEntity<ProformaInvoiceResponse> updateStatut(
            @PathVariable UUID id,
            @RequestParam StatutProforma statut) {
        return ResponseEntity.ok(proformaService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une facture proforma")
    public ResponseEntity<Void> deleteProforma(@PathVariable UUID id) {
        proformaService.deleteProforma(id);
        return ResponseEntity.noContent().build();
    }
}
