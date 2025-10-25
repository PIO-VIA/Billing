package com.example.account.controller;

import com.example.account.dto.request.BonAchatCreateRequest;
import com.example.account.dto.request.BonAchatUpdateRequest;
import com.example.account.dto.response.BonAchatResponse;
import com.example.account.service.BonAchatService;
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
@RequestMapping("/api/bons-achat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bon d'Achat", description = "API de gestion des bons d'achat")
public class BonAchatController {

    private final BonAchatService bonAchatService;

    @PostMapping
    @Operation(summary = "Créer un nouveau bon d'achat")
    public ResponseEntity<BonAchatResponse> createBonAchat(@Valid @RequestBody BonAchatCreateRequest request) {
        log.info("Requête de création de bon d'achat: {}", request.getNumeroBon());
        BonAchatResponse response = bonAchatService.createBonAchat(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{bonAchatId}")
    @Operation(summary = "Mettre à jour un bon d'achat")
    public ResponseEntity<BonAchatResponse> updateBonAchat(
            @PathVariable UUID bonAchatId,
            @Valid @RequestBody BonAchatUpdateRequest request) {
        log.info("Requête de mise à jour du bon d'achat: {}", bonAchatId);
        BonAchatResponse response = bonAchatService.updateBonAchat(bonAchatId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bonAchatId}")
    @Operation(summary = "Récupérer un bon d'achat par ID")
    public ResponseEntity<BonAchatResponse> getBonAchatById(@PathVariable UUID bonAchatId) {
        log.info("Requête de récupération du bon d'achat: {}", bonAchatId);
        BonAchatResponse response = bonAchatService.getBonAchatById(bonAchatId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numeroBon}")
    @Operation(summary = "Récupérer un bon d'achat par numéro")
    public ResponseEntity<BonAchatResponse> getBonAchatByNumero(@PathVariable String numeroBon) {
        log.info("Requête de récupération du bon d'achat par numéro: {}", numeroBon);
        BonAchatResponse response = bonAchatService.getBonAchatByNumero(numeroBon);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les bons d'achat")
    public ResponseEntity<List<BonAchatResponse>> getAllBonAchats() {
        log.info("Requête de récupération de tous les bons d'achat");
        List<BonAchatResponse> responses = bonAchatService.getAllBonAchats();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer tous les bons d'achat avec pagination")
    public ResponseEntity<Page<BonAchatResponse>> getAllBonAchatsPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les bons d'achat avec pagination");
        Page<BonAchatResponse> responses = bonAchatService.getAllBonAchats(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fournisseur/{idFournisseur}")
    @Operation(summary = "Récupérer les bons d'achat par fournisseur")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByFournisseur(@PathVariable UUID idFournisseur) {
        log.info("Requête de récupération des bons d'achat du fournisseur: {}", idFournisseur);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByFournisseur(idFournisseur);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/bon-commande/{idBonCommande}")
    @Operation(summary = "Récupérer les bons d'achat par bon de commande")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByBonCommande(@PathVariable UUID idBonCommande) {
        log.info("Requête de récupération des bons d'achat pour le bon de commande: {}", idBonCommande);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByBonCommande(idBonCommande);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les bons d'achat par statut")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByStatut(@PathVariable String statut) {
        log.info("Requête de récupération des bons d'achat par statut: {}", statut);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByStatut(statut);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/devise/{devise}")
    @Operation(summary = "Récupérer les bons d'achat par devise")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByDevise(@PathVariable String devise) {
        log.info("Requête de récupération des bons d'achat par devise: {}", devise);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByDevise(devise);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-achat")
    @Operation(summary = "Récupérer les bons d'achat par période")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByDateAchat(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des bons d'achat entre {} et {}", startDate, endDate);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByDateAchatBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-livraison")
    @Operation(summary = "Récupérer les bons d'achat par période de livraison")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByDateLivraison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Requête de récupération des bons d'achat avec livraison entre {} et {}", startDate, endDate);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByDateLivraisonBetween(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/montant-range")
    @Operation(summary = "Récupérer les bons d'achat par fourchette de montant")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByMontantRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        log.info("Requête de récupération des bons d'achat entre {} et {}", minAmount, maxAmount);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByMontantBetween(minAmount, maxAmount);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fournisseur/{idFournisseur}/statut/{statut}")
    @Operation(summary = "Récupérer les bons d'achat par fournisseur et statut")
    public ResponseEntity<List<BonAchatResponse>> getBonAchatsByFournisseurAndStatut(
            @PathVariable UUID idFournisseur,
            @PathVariable String statut) {
        log.info("Requête de récupération des bons d'achat du fournisseur {} avec statut {}", idFournisseur, statut);
        List<BonAchatResponse> responses = bonAchatService.getBonAchatsByFournisseurAndStatut(idFournisseur, statut);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/numero")
    @Operation(summary = "Rechercher des bons d'achat par numéro")
    public ResponseEntity<List<BonAchatResponse>> searchBonAchatsByNumero(@RequestParam String numero) {
        log.info("Requête de recherche des bons d'achat par numéro: {}", numero);
        List<BonAchatResponse> responses = bonAchatService.searchBonAchatsByNumero(numero);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/fournisseur")
    @Operation(summary = "Rechercher des bons d'achat par fournisseur")
    public ResponseEntity<List<BonAchatResponse>> searchBonAchatsByFournisseur(@RequestParam String fournisseur) {
        log.info("Requête de recherche des bons d'achat par fournisseur: {}", fournisseur);
        List<BonAchatResponse> responses = bonAchatService.searchBonAchatsByFournisseur(fournisseur);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/facture-fournisseur")
    @Operation(summary = "Rechercher des bons d'achat par facture fournisseur")
    public ResponseEntity<List<BonAchatResponse>> searchBonAchatsByFactureFournisseur(@RequestParam String numeroFacture) {
        log.info("Requête de recherche des bons d'achat par facture fournisseur: {}", numeroFacture);
        List<BonAchatResponse> responses = bonAchatService.searchBonAchatsByFactureFournisseur(numeroFacture);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{bonAchatId}")
    @Operation(summary = "Supprimer un bon d'achat")
    public ResponseEntity<Void> deleteBonAchat(@PathVariable UUID bonAchatId) {
        log.info("Requête de suppression du bon d'achat: {}", bonAchatId);
        bonAchatService.deleteBonAchat(bonAchatId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total/fournisseur/{idFournisseur}")
    @Operation(summary = "Calculer le montant total des achats par fournisseur")
    public ResponseEntity<BigDecimal> getTotalMontantByFournisseur(@PathVariable UUID idFournisseur) {
        log.info("Requête de calcul du montant total pour le fournisseur: {}", idFournisseur);
        BigDecimal total = bonAchatService.getTotalMontantByFournisseur(idFournisseur);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/statut/{statut}")
    @Operation(summary = "Calculer le montant total des achats par statut")
    public ResponseEntity<BigDecimal> getTotalMontantByStatut(@PathVariable String statut) {
        log.info("Requête de calcul du montant total par statut: {}", statut);
        BigDecimal total = bonAchatService.getTotalMontantByStatut(statut);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/count/statut/{statut}")
    @Operation(summary = "Compter les bons d'achat par statut")
    public ResponseEntity<Long> countByStatut(@PathVariable String statut) {
        log.info("Requête de comptage des bons d'achat par statut: {}", statut);
        Long count = bonAchatService.countByStatut(statut);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/fournisseur/{idFournisseur}")
    @Operation(summary = "Compter les bons d'achat par fournisseur")
    public ResponseEntity<Long> countByFournisseur(@PathVariable UUID idFournisseur) {
        log.info("Requête de comptage des bons d'achat pour le fournisseur: {}", idFournisseur);
        Long count = bonAchatService.countByFournisseur(idFournisseur);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{bonAchatId}/statut")
    @Operation(summary = "Mettre à jour le statut d'un bon d'achat")
    public ResponseEntity<BonAchatResponse> updateStatut(
            @PathVariable UUID bonAchatId,
            @RequestParam String statut) {
        log.info("Requête de mise à jour du statut du bon d'achat {} vers {}", bonAchatId, statut);
        BonAchatResponse response = bonAchatService.updateStatut(bonAchatId, statut);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bonAchatId}/valider")
    @Operation(summary = "Valider un bon d'achat")
    public ResponseEntity<BonAchatResponse> validerBonAchat(
            @PathVariable UUID bonAchatId,
            @RequestParam String validatedBy) {
        log.info("Requête de validation du bon d'achat: {}", bonAchatId);
        BonAchatResponse response = bonAchatService.validerBonAchat(bonAchatId, validatedBy);
        return ResponseEntity.ok(response);
    }
}
