package com.example.account.controller;

import com.example.account.dto.request.ProduitCreateRequest;
import com.example.account.dto.request.ProduitUpdateRequest;
import com.example.account.dto.response.ProduitResponse;
import com.example.account.service.ProduitService;
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
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Produit", description = "API de gestion des produits")
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @Operation(summary = "Créer un nouveau produit")
    public ResponseEntity<ProduitResponse> createProduit(@Valid @RequestBody ProduitCreateRequest request) {
        log.info("Requête de création de produit: {}", request.getNomProduit());
        ProduitResponse response = produitService.createProduit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{produitId}")
    @Operation(summary = "Mettre à jour un produit")
    public ResponseEntity<ProduitResponse> updateProduit(
            @PathVariable UUID produitId,
            @Valid @RequestBody ProduitUpdateRequest request) {
        log.info("Requête de mise à jour du produit: {}", produitId);
        ProduitResponse response = produitService.updateProduit(produitId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{produitId}")
    @Operation(summary = "Récupérer un produit par ID")
    public ResponseEntity<ProduitResponse> getProduitById(@PathVariable UUID produitId) {
        log.info("Requête de récupération du produit: {}", produitId);
        ProduitResponse response = produitService.getProduitById(produitId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Récupérer un produit par référence")
    public ResponseEntity<ProduitResponse> getProduitByReference(@PathVariable String reference) {
        log.info("Requête de récupération du produit par référence: {}", reference);
        ProduitResponse response = produitService.getProduitByReference(reference);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code-barre/{codeBarre}")
    @Operation(summary = "Récupérer un produit par code-barre")
    public ResponseEntity<ProduitResponse> getProduitByCodeBarre(@PathVariable String codeBarre) {
        log.info("Requête de récupération du produit par code-barre: {}", codeBarre);
        ProduitResponse response = produitService.getProduitByCodeBarre(codeBarre);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les produits")
    public ResponseEntity<List<ProduitResponse>> getAllProduits() {
        log.info("Requête de récupération de tous les produits");
        List<ProduitResponse> responses = produitService.getAllProduits();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer tous les produits avec pagination")
    public ResponseEntity<Page<ProduitResponse>> getAllProduitsPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les produits avec pagination");
        Page<ProduitResponse> responses = produitService.getAllProduits(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/actifs")
    @Operation(summary = "Récupérer tous les produits actifs")
    public ResponseEntity<List<ProduitResponse>> getActiveProduits() {
        log.info("Requête de récupération des produits actifs");
        List<ProduitResponse> responses = produitService.getActiveProduits();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Récupérer les produits par catégorie")
    public ResponseEntity<List<ProduitResponse>> getProduitsByCategorie(@PathVariable String categorie) {
        log.info("Requête de récupération des produits par catégorie: {}", categorie);
        List<ProduitResponse> responses = produitService.getProduitsByCategorie(categorie);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{typeProduit}")
    @Operation(summary = "Récupérer les produits par type")
    public ResponseEntity<List<ProduitResponse>> getProduitsByType(@PathVariable String typeProduit) {
        log.info("Requête de récupération des produits par type: {}", typeProduit);
        List<ProduitResponse> responses = produitService.getProduitsByType(typeProduit);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des produits par nom")
    public ResponseEntity<List<ProduitResponse>> searchProduitsByNom(@RequestParam String nom) {
        log.info("Requête de recherche des produits par nom: {}", nom);
        List<ProduitResponse> responses = produitService.searchProduitsByNom(nom);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/prix-range")
    @Operation(summary = "Récupérer les produits par fourchette de prix")
    public ResponseEntity<List<ProduitResponse>> getProduitsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.info("Requête de récupération des produits entre {} et {}", minPrice, maxPrice);
        List<ProduitResponse> responses = produitService.getProduitsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{produitId}")
    @Operation(summary = "Supprimer un produit")
    public ResponseEntity<Void> deleteProduit(@PathVariable UUID produitId) {
        log.info("Requête de suppression du produit: {}", produitId);
        produitService.deleteProduit(produitId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{produitId}/desactiver")
    @Operation(summary = "Désactiver un produit")
    public ResponseEntity<ProduitResponse> desactiverProduit(@PathVariable UUID produitId) {
        log.info("Requête de désactivation du produit: {}", produitId);
        ProduitResponse response = produitService.desactiverProduit(produitId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{produitId}/activer")
    @Operation(summary = "Activer un produit")
    public ResponseEntity<ProduitResponse> activerProduit(@PathVariable UUID produitId) {
        log.info("Requête d'activation du produit: {}", produitId);
        ProduitResponse response = produitService.activerProduit(produitId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/categorie/{categorie}")
    @Operation(summary = "Compter les produits par catégorie")
    public ResponseEntity<Long> countByCategorie(@PathVariable String categorie) {
        log.info("Requête de comptage des produits par catégorie: {}", categorie);
        Long count = produitService.countByCategorie(categorie);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/type/{typeProduit}")
    @Operation(summary = "Compter les produits par type")
    public ResponseEntity<Long> countByType(@PathVariable String typeProduit) {
        log.info("Requête de comptage des produits par type: {}", typeProduit);
        Long count = produitService.countByType(typeProduit);
        return ResponseEntity.ok(count);
    }
}
