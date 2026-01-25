package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.TaxeCreateRequest;
import com.example.account.modules.facturation.dto.request.TaxeUpdateRequest;
import com.example.account.modules.facturation.dto.response.TaxeResponse;
import com.example.account.modules.facturation.service.TaxeService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Taxe", description = "API de gestion des taxes")
public class TaxeController {

    private final TaxeService taxeService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle taxe")
    public ResponseEntity<TaxeResponse> createTaxe(@Valid @RequestBody TaxeCreateRequest request) {
        log.info("Requête de création de taxe: {}", request.getNomTaxe());
        TaxeResponse response = taxeService.createTaxe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{taxeId}")
    @Operation(summary = "Mettre à jour une taxe")
    public ResponseEntity<TaxeResponse> updateTaxe(
            @PathVariable UUID taxeId,
            @Valid @RequestBody TaxeUpdateRequest request) {
        log.info("Requête de mise à jour de la taxe: {}", taxeId);
        TaxeResponse response = taxeService.updateTaxe(taxeId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taxeId}")
    @Operation(summary = "Récupérer une taxe par ID")
    public ResponseEntity<TaxeResponse> getTaxeById(@PathVariable UUID taxeId) {
        log.info("Requête de récupération de la taxe: {}", taxeId);
        TaxeResponse response = taxeService.getTaxeById(taxeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nom/{nomTaxe}")
    @Operation(summary = "Récupérer une taxe par nom")
    public ResponseEntity<TaxeResponse> getTaxeByNom(@PathVariable String nomTaxe) {
        log.info("Requête de récupération de la taxe par nom: {}", nomTaxe);
        TaxeResponse response = taxeService.getTaxeByNom(nomTaxe);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les taxes")
    public ResponseEntity<List<TaxeResponse>> getAllTaxes() {
        log.info("Requête de récupération de toutes les taxes");
        List<TaxeResponse> responses = taxeService.getAllTaxes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer toutes les taxes (paginée)")
    public ResponseEntity<Page<TaxeResponse>> getAllTaxesPaginated(Pageable pageable) {
        log.info("Requête de récupération de toutes les taxes (paginée)");
        Page<TaxeResponse> responses = taxeService.getAllTaxes(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/actives")
    @Operation(summary = "Récupérer toutes les taxes actives")
    public ResponseEntity<List<TaxeResponse>> getActiveTaxes() {
        log.info("Requête de récupération des taxes actives");
        List<TaxeResponse> responses = taxeService.getActiveTaxes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{typeTaxe}")
    @Operation(summary = "Récupérer les taxes par type")
    public ResponseEntity<List<TaxeResponse>> getTaxesByType(@PathVariable String typeTaxe) {
        log.info("Requête de récupération des taxes par type: {}", typeTaxe);
        List<TaxeResponse> responses = taxeService.getTaxesByType(typeTaxe);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{typeTaxe}/actives")
    @Operation(summary = "Récupérer les taxes actives par type")
    public ResponseEntity<List<TaxeResponse>> getActiveTaxesByType(@PathVariable String typeTaxe) {
        log.info("Requête de récupération des taxes actives par type: {}", typeTaxe);
        List<TaxeResponse> responses = taxeService.getActiveTaxesByType(typeTaxe);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/porte/{porteTaxe}")
    @Operation(summary = "Récupérer les taxes par portée")
    public ResponseEntity<List<TaxeResponse>> getTaxesByPorte(@PathVariable String porteTaxe) {
        log.info("Requête de récupération des taxes par portée: {}", porteTaxe);
        List<TaxeResponse> responses = taxeService.getTaxesByPorte(porteTaxe);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/position/{positionFiscale}")
    @Operation(summary = "Récupérer les taxes par position fiscale")
    public ResponseEntity<List<TaxeResponse>> getTaxesByPositionFiscale(@PathVariable String positionFiscale) {
        log.info("Requête de récupération des taxes par position fiscale: {}", positionFiscale);
        List<TaxeResponse> responses = taxeService.getTaxesByPositionFiscale(positionFiscale);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/calcul-range")
    @Operation(summary = "Récupérer les taxes par plage de calcul")
    public ResponseEntity<List<TaxeResponse>> getTaxesByCalculRange(
            @RequestParam BigDecimal minTaux,
            @RequestParam BigDecimal maxTaux) {
        log.info("Requête de récupération des taxes par plage de calcul: {} - {}", minTaux, maxTaux);
        List<TaxeResponse> responses = taxeService.getTaxesByCalculRange(minTaux, maxTaux);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/montant-range")
    @Operation(summary = "Récupérer les taxes par plage de montant")
    public ResponseEntity<List<TaxeResponse>> getTaxesByMontantRange(
            @RequestParam BigDecimal minMontant,
            @RequestParam BigDecimal maxMontant) {
        log.info("Requête de récupération des taxes par plage de montant: {} - {}", minMontant, maxMontant);
        List<TaxeResponse> responses = taxeService.getTaxesByMontantRange(minMontant, maxMontant);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{taxeId}")
    @Operation(summary = "Supprimer une taxe")
    public ResponseEntity<Void> deleteTaxe(@PathVariable UUID taxeId) {
        log.info("Requête de suppression de la taxe: {}", taxeId);
        taxeService.deleteTaxe(taxeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taxeId}/activer")
    @Operation(summary = "Activer une taxe")
    public ResponseEntity<TaxeResponse> activerTaxe(@PathVariable UUID taxeId) {
        log.info("Requête d'activation de la taxe: {}", taxeId);
        TaxeResponse response = taxeService.activerTaxe(taxeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taxeId}/desactiver")
    @Operation(summary = "Désactiver une taxe")
    public ResponseEntity<TaxeResponse> desactiverTaxe(@PathVariable UUID taxeId) {
        log.info("Requête de désactivation de la taxe: {}", taxeId);
        TaxeResponse response = taxeService.desactiverTaxe(taxeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/actives")
    @Operation(summary = "Compter les taxes actives")
    public ResponseEntity<Long> countActiveTaxes() {
        log.info("Requête de comptage des taxes actives");
        Long count = taxeService.countActiveTaxes();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/type/{typeTaxe}")
    @Operation(summary = "Compter les taxes par type")
    public ResponseEntity<Long> countByType(@PathVariable String typeTaxe) {
        log.info("Requête de comptage des taxes par type: {}", typeTaxe);
        Long count = taxeService.countByType(typeTaxe);
        return ResponseEntity.ok(count);
    }
}
