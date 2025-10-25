package com.example.account.controller;

import com.example.account.dto.request.BonCommandeCreateRequest;
import com.example.account.dto.request.BonCommandeUpdateRequest;
import com.example.account.dto.response.BonCommandeResponse;
import com.example.account.service.BonCommandeService;
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
@RequestMapping("/api/bons-commande")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bon de Commande", description = "API de gestion des bons de commande")
public class BonCommandeController {

    private final BonCommandeService bonCommandeService;

    @PostMapping
    @Operation(summary = "Créer un nouveau bon de commande")
    public ResponseEntity<BonCommandeResponse> createBonCommande(@Valid @RequestBody BonCommandeCreateRequest request) {
        log.info("Requête de création de bon de commande: {}", request.getNumeroCommande());
        BonCommandeResponse response = bonCommandeService.createBonCommande(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{bonCommandeId}")
    @Operation(summary = "Mettre à jour un bon de commande")
    public ResponseEntity<BonCommandeResponse> updateBonCommande(
            @PathVariable UUID bonCommandeId,
            @Valid @RequestBody BonCommandeUpdateRequest request) {
        log.info("Requête de mise à jour du bon de commande: {}", bonCommandeId);
        BonCommandeResponse response = bonCommandeService.updateBonCommande(bonCommandeId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bonCommandeId}")
    @Operation(summary = "Récupérer un bon de commande par ID")
    public ResponseEntity<BonCommandeResponse> getBonCommandeById(@PathVariable UUID bonCommandeId) {
        log.info("Requête de récupération du bon de commande: {}", bonCommandeId);
        BonCommandeResponse response = bonCommandeService.getBonCommandeById(bonCommandeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numeroCommande}")
    @Operation(summary = "Récupérer un bon de commande par numéro")
    public ResponseEntity<BonCommandeResponse> getBonCommandeByNumero(@PathVariable String numeroCommande) {
        log.info("Requête de récupération du bon de commande par numéro: {}", numeroCommande);
        BonCommandeResponse response = bonCommandeService.getBonCommandeByNumero(numeroCommande);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les bons de commande")
    public ResponseEntity<List<BonCommandeResponse>> getAllBonCommandes() {
        log.info("Requête de récupération de tous les bons de commande");
        List<BonCommandeResponse> responses = bonCommandeService.getAllBonCommandes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer tous les bons de commande avec pagination")
    public ResponseEntity<Page<BonCommandeResponse>> getAllBonCommandesPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les bons de commande avec pagination");
        Page<BonCommandeResponse> responses = bonCommandeService.getAllBonCommandes(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fournisseur/{idFournisseur}")
    @Operation(summary = "Récupérer les bons de commande par fournisseur")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByFournisseur(@PathVariable UUID idFournisseur) {
        log.info("Requête de récupération des bons de commande du fournisseur: {}", idFournisseur);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByFournisseur(idFournisseur);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les bons de commande par statut")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByStatut(@PathVariable String statut) {
        log.info("Requête de récupération des bons de commande par statut: {}", statut);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByStatut(statut);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/devise/{devise}")
    @Operation(summary = "Récupérer les bons de commande par devise")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByDevise(@PathVariable String devise) {
        log.info("Requête de récupération des bons de commande par devise: {}", devise);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByDevise(devise);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-commande")
    @Operation(summary = "Récupérer les bons de commande par période")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByDateCommande(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des bons de commande entre {} et {}", startDate, endDate);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByDateCommandeBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-livraison")
    @Operation(summary = "Récupérer les bons de commande par période de livraison")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByDateLivraison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des bons de commande avec livraison entre {} et {}", startDate, endDate);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByDateLivraisonBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/montant-range")
    @Operation(summary = "Récupérer les bons de commande par fourchette de montant")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByMontantRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        log.info("Requête de récupération des bons de commande entre {} et {}", minAmount, maxAmount);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByMontantBetween(minAmount, maxAmount);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fournisseur/{idFournisseur}/statut/{statut}")
    @Operation(summary = "Récupérer les bons de commande par fournisseur et statut")
    public ResponseEntity<List<BonCommandeResponse>> getBonCommandesByFournisseurAndStatut(
            @PathVariable UUID idFournisseur,
            @PathVariable String statut) {
        log.info("Requête de récupération des bons de commande du fournisseur {} avec statut {}", idFournisseur, statut);
        List<BonCommandeResponse> responses = bonCommandeService.getBonCommandesByFournisseurAndStatut(idFournisseur, statut);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/numero")
    @Operation(summary = "Rechercher des bons de commande par numéro")
    public ResponseEntity<List<BonCommandeResponse>> searchBonCommandesByNumero(@RequestParam String numero) {
        log.info("Requête de recherche des bons de commande par numéro: {}", numero);
        List<BonCommandeResponse> responses = bonCommandeService.searchBonCommandesByNumero(numero);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/fournisseur")
    @Operation(summary = "Rechercher des bons de commande par fournisseur")
    public ResponseEntity<List<BonCommandeResponse>> searchBonCommandesByFournisseur(@RequestParam String fournisseur) {
        log.info("Requête de recherche des bons de commande par fournisseur: {}", fournisseur);
        List<BonCommandeResponse> responses = bonCommandeService.searchBonCommandesByFournisseur(fournisseur);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{bonCommandeId}")
    @Operation(summary = "Supprimer un bon de commande")
    public ResponseEntity<Void> deleteBonCommande(@PathVariable UUID bonCommandeId) {
        log.info("Requête de suppression du bon de commande: {}", bonCommandeId);
        bonCommandeService.deleteBonCommande(bonCommandeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total/fournisseur/{idFournisseur}")
    @Operation(summary = "Calculer le montant total des commandes par fournisseur")
    public ResponseEntity<BigDecimal> getTotalMontantByFournisseur(@PathVariable UUID idFournisseur) {
        log.info("Requête de calcul du montant total pour le fournisseur: {}", idFournisseur);
        BigDecimal total = bonCommandeService.getTotalMontantByFournisseur(idFournisseur);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/statut/{statut}")
    @Operation(summary = "Calculer le montant total des commandes par statut")
    public ResponseEntity<BigDecimal> getTotalMontantByStatut(@PathVariable String statut) {
        log.info("Requête de calcul du montant total par statut: {}", statut);
        BigDecimal total = bonCommandeService.getTotalMontantByStatut(statut);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/count/statut/{statut}")
    @Operation(summary = "Compter les bons de commande par statut")
    public ResponseEntity<Long> countByStatut(@PathVariable String statut) {
        log.info("Requête de comptage des bons de commande par statut: {}", statut);
        Long count = bonCommandeService.countByStatut(statut);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/fournisseur/{idFournisseur}")
    @Operation(summary = "Compter les bons de commande par fournisseur")
    public ResponseEntity<Long> countByFournisseur(@PathVariable UUID idFournisseur) {
        log.info("Requête de comptage des bons de commande pour le fournisseur: {}", idFournisseur);
        Long count = bonCommandeService.countByFournisseur(idFournisseur);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{bonCommandeId}/statut")
    @Operation(summary = "Mettre à jour le statut d'un bon de commande")
    public ResponseEntity<BonCommandeResponse> updateStatut(
            @PathVariable UUID bonCommandeId,
            @RequestParam String statut) {
        log.info("Requête de mise à jour du statut du bon de commande {} vers {}", bonCommandeId, statut);
        BonCommandeResponse response = bonCommandeService.updateStatut(bonCommandeId, statut);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bonCommandeId}/valider")
    @Operation(summary = "Valider un bon de commande")
    public ResponseEntity<BonCommandeResponse> validerBonCommande(
            @PathVariable UUID bonCommandeId,
            @RequestParam String validatedBy) {
        log.info("Requête de validation du bon de commande: {}", bonCommandeId);
        BonCommandeResponse response = bonCommandeService.validerBonCommande(bonCommandeId, validatedBy);
        return ResponseEntity.ok(response);
    }
}
