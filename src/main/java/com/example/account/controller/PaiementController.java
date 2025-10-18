package com.example.account.controller;

import com.example.account.dto.request.PaiementCreateRequest;
import com.example.account.dto.request.PaiementUpdateRequest;
import com.example.account.dto.response.PaiementResponse;
import com.example.account.model.enums.TypePaiement;
import com.example.account.service.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Paiement", description = "API de gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement")
    public ResponseEntity<PaiementResponse> createPaiement(@Valid @RequestBody PaiementCreateRequest request) {
        log.info("Requête de création de paiement pour le client: {}", request.getIdClient());
        PaiementResponse response = paiementService.createPaiement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{paiementId}")
    @Operation(summary = "Mettre à jour un paiement")
    public ResponseEntity<PaiementResponse> updatePaiement(
            @PathVariable UUID paiementId,
            @Valid @RequestBody PaiementUpdateRequest request) {
        log.info("Requête de mise à jour du paiement: {}", paiementId);
        PaiementResponse response = paiementService.updatePaiement(paiementId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paiementId}")
    @Operation(summary = "Récupérer un paiement par ID")
    public ResponseEntity<PaiementResponse> getPaiementById(@PathVariable UUID paiementId) {
        log.info("Requête de récupération du paiement: {}", paiementId);
        PaiementResponse response = paiementService.getPaiementById(paiementId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les paiements")
    public ResponseEntity<List<PaiementResponse>> getAllPaiements() {
        log.info("Requête de récupération de tous les paiements");
        List<PaiementResponse> responses = paiementService.getAllPaiements();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Récupérer tous les paiements avec pagination")
    public ResponseEntity<Page<PaiementResponse>> getAllPaiementsPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les paiements avec pagination");
        Page<PaiementResponse> responses = paiementService.getAllPaiements(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les paiements d'un client")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByClient(@PathVariable UUID clientId) {
        log.info("Requête de récupération des paiements du client: {}", clientId);
        List<PaiementResponse> responses = paiementService.getPaiementsByClient(clientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/facture/{factureId}")
    @Operation(summary = "Récupérer les paiements d'une facture")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByFacture(@PathVariable UUID factureId) {
        log.info("Requête de récupération des paiements de la facture: {}", factureId);
        List<PaiementResponse> responses = paiementService.getPaiementsByFacture(factureId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/mode/{modePaiement}")
    @Operation(summary = "Récupérer les paiements par mode de paiement")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByModePaiement(@PathVariable TypePaiement modePaiement) {
        log.info("Requête de récupération des paiements par mode: {}", modePaiement);
        List<PaiementResponse> responses = paiementService.getPaiementsByModePaiement(modePaiement);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les paiements par période")
    public ResponseEntity<List<PaiementResponse>> getPaiementsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Requête de récupération des paiements entre {} et {}", dateDebut, dateFin);
        List<PaiementResponse> responses = paiementService.getPaiementsByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{paiementId}")
    @Operation(summary = "Supprimer un paiement")
    public ResponseEntity<Void> deletePaiement(@PathVariable UUID paiementId) {
        log.info("Requête de suppression du paiement: {}", paiementId);
        paiementService.deletePaiement(paiementId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total/client/{clientId}")
    @Operation(summary = "Obtenir le total des paiements d'un client")
    public ResponseEntity<BigDecimal> getTotalPaiementsByClient(@PathVariable UUID clientId) {
        log.info("Requête de calcul du total des paiements du client: {}", clientId);
        BigDecimal total = paiementService.getTotalPaiementsByClient(clientId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/facture/{factureId}")
    @Operation(summary = "Obtenir le total des paiements d'une facture")
    public ResponseEntity<BigDecimal> getTotalPaiementsByFacture(@PathVariable UUID factureId) {
        log.info("Requête de calcul du total des paiements de la facture: {}", factureId);
        BigDecimal total = paiementService.getTotalPaiementsByFacture(factureId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/periode")
    @Operation(summary = "Obtenir le total des paiements par période")
    public ResponseEntity<BigDecimal> getTotalPaiementsByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Requête de calcul du total des paiements entre {} et {}", dateDebut, dateFin);
        BigDecimal total = paiementService.getTotalPaiementsByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(total);
    }
}
