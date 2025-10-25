package com.example.account.controller;

import com.example.account.dto.request.RemboursementCreateRequest;
import com.example.account.dto.request.RemboursementUpdateRequest;
import com.example.account.dto.response.RemboursementResponse;
import com.example.account.service.RemboursementService;
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
@RequestMapping("/api/remboursements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Remboursement", description = "API de gestion des remboursements")
public class RemboursementController {

    private final RemboursementService remboursementService;

    @PostMapping
    @Operation(summary = "Créer un nouveau remboursement")
    public ResponseEntity<RemboursementResponse> createRemboursement(@Valid @RequestBody RemboursementCreateRequest request) {
        log.info("Requête de création de remboursement pour le client: {}", request.getIdClient());
        RemboursementResponse response = remboursementService.createRemboursement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{remboursementId}")
    @Operation(summary = "Mettre à jour un remboursement")
    public ResponseEntity<RemboursementResponse> updateRemboursement(
            @PathVariable UUID remboursementId,
            @Valid @RequestBody RemboursementUpdateRequest request) {
        log.info("Requête de mise à jour du remboursement: {}", remboursementId);
        RemboursementResponse response = remboursementService.updateRemboursement(remboursementId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{remboursementId}")
    @Operation(summary = "Récupérer un remboursement par ID")
    public ResponseEntity<RemboursementResponse> getRemboursementById(@PathVariable UUID remboursementId) {
        log.info("Requête de récupération du remboursement: {}", remboursementId);
        RemboursementResponse response = remboursementService.getRemboursementById(remboursementId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les remboursements")
    public ResponseEntity<List<RemboursementResponse>> getAllRemboursements() {
        log.info("Requête de récupération de tous les remboursements");
        List<RemboursementResponse> responses = remboursementService.getAllRemboursements();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer tous les remboursements avec pagination")
    public ResponseEntity<Page<RemboursementResponse>> getAllRemboursementsPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les remboursements avec pagination");
        Page<RemboursementResponse> responses = remboursementService.getAllRemboursements(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{idClient}")
    @Operation(summary = "Récupérer les remboursements par client")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByClient(@PathVariable UUID idClient) {
        log.info("Requête de récupération des remboursements du client: {}", idClient);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByClient(idClient);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/facture/{idFacture}")
    @Operation(summary = "Récupérer les remboursements par facture")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByFacture(@PathVariable UUID idFacture) {
        log.info("Requête de récupération des remboursements de la facture: {}", idFacture);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByFacture(idFacture);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les remboursements par statut")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByStatut(@PathVariable String statut) {
        log.info("Requête de récupération des remboursements par statut: {}", statut);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByStatut(statut);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/devise/{devise}")
    @Operation(summary = "Récupérer les remboursements par devise")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByDevise(@PathVariable String devise) {
        log.info("Requête de récupération des remboursements par devise: {}", devise);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByDevise(devise);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-facturation")
    @Operation(summary = "Récupérer les remboursements par période de facturation")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByDateFacturation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des remboursements entre {} et {}", startDate, endDate);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByDateFacturationBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-echeance")
    @Operation(summary = "Récupérer les remboursements par période d'échéance")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByDateEcheance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des remboursements avec échéance entre {} et {}", startDate, endDate);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByDateEcheanceBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-comptable")
    @Operation(summary = "Récupérer les remboursements par période comptable")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByDateComptable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des remboursements avec date comptable entre {} et {}", startDate, endDate);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByDateComptableBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/montant-range")
    @Operation(summary = "Récupérer les remboursements par fourchette de montant")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByMontantRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        log.info("Requête de récupération des remboursements entre {} et {}", minAmount, maxAmount);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByMontantBetween(minAmount, maxAmount);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/retard")
    @Operation(summary = "Récupérer les remboursements en retard")
    public ResponseEntity<List<RemboursementResponse>> getOverdueRemboursements() {
        log.info("Requête de récupération des remboursements en retard");
        List<RemboursementResponse> responses = remboursementService.getOverdueRemboursements();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{idClient}/statut/{statut}")
    @Operation(summary = "Récupérer les remboursements par client et statut")
    public ResponseEntity<List<RemboursementResponse>> getRemboursementsByClientAndStatut(
            @PathVariable UUID idClient,
            @PathVariable String statut) {
        log.info("Requête de récupération des remboursements du client {} avec statut {}", idClient, statut);
        List<RemboursementResponse> responses = remboursementService.getRemboursementsByClientAndStatut(idClient, statut);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{remboursementId}")
    @Operation(summary = "Supprimer un remboursement")
    public ResponseEntity<Void> deleteRemboursement(@PathVariable UUID remboursementId) {
        log.info("Requête de suppression du remboursement: {}", remboursementId);
        remboursementService.deleteRemboursement(remboursementId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total/client/{idClient}")
    @Operation(summary = "Calculer le montant total des remboursements par client")
    public ResponseEntity<BigDecimal> getTotalMontantByClient(@PathVariable UUID idClient) {
        log.info("Requête de calcul du montant total pour le client: {}", idClient);
        BigDecimal total = remboursementService.getTotalMontantByClient(idClient);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/statut/{statut}")
    @Operation(summary = "Calculer le montant total des remboursements par statut")
    public ResponseEntity<BigDecimal> getTotalMontantByStatut(@PathVariable String statut) {
        log.info("Requête de calcul du montant total par statut: {}", statut);
        BigDecimal total = remboursementService.getTotalMontantByStatut(statut);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/count/statut/{statut}")
    @Operation(summary = "Compter les remboursements par statut")
    public ResponseEntity<Long> countByStatut(@PathVariable String statut) {
        log.info("Requête de comptage des remboursements par statut: {}", statut);
        Long count = remboursementService.countByStatut(statut);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/client/{idClient}")
    @Operation(summary = "Compter les remboursements par client")
    public ResponseEntity<Long> countByClient(@PathVariable UUID idClient) {
        log.info("Requête de comptage des remboursements pour le client: {}", idClient);
        Long count = remboursementService.countByClient(idClient);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{remboursementId}/statut")
    @Operation(summary = "Mettre à jour le statut d'un remboursement")
    public ResponseEntity<RemboursementResponse> updateStatut(
            @PathVariable UUID remboursementId,
            @RequestParam String statut) {
        log.info("Requête de mise à jour du statut du remboursement {} vers {}", remboursementId, statut);
        RemboursementResponse response = remboursementService.updateStatut(remboursementId, statut);
        return ResponseEntity.ok(response);
    }
}
