package com.example.account.controller;

import com.example.account.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "API d'analyses et rapports")
public class AnalyticsController {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final StockRepository stockRepository;

    @GetMapping("/ventes/periode")
    @Operation(summary = "Rapport des ventes par période")
    public ResponseEntity<Map<String, Object>> getRapportVentes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Rapport des ventes entre {} et {}", startDate, endDate);

        BigDecimal montantTotal = factureRepository.sumMontantByDateBetween(startDate, endDate);
        Long nombreFactures = factureRepository.countByDateBetween(startDate, endDate);

        Map<String, Object> rapport = new HashMap<>();
        rapport.put("periode", Map.of("debut", startDate, "fin", endDate));
        rapport.put("montantTotal", montantTotal != null ? montantTotal : BigDecimal.ZERO);
        rapport.put("nombreFactures", nombreFactures != null ? nombreFactures : 0L);
        rapport.put("montantMoyen", nombreFactures != null && nombreFactures > 0
            ? (montantTotal != null ? montantTotal.divide(BigDecimal.valueOf(nombreFactures), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO)
            : BigDecimal.ZERO);

        return ResponseEntity.ok(rapport);
    }

    @GetMapping("/stocks/alertes")
    @Operation(summary = "Stocks avec alertes (sous le minimum)")
    public ResponseEntity<Map<String, Object>> getStocksAlertes() {
        log.info("Récupération des stocks en alerte");

        var stocksSousMinimum = stockRepository.findStocksSousMinimum();
        var stocksCritiques = stockRepository.findStocksAvecSeuilCritique(BigDecimal.ZERO);

        Map<String, Object> rapport = new HashMap<>();
        rapport.put("stocksSousMinimum", stocksSousMinimum);
        rapport.put("stocksCritiques", stocksCritiques);
        rapport.put("nombreAlertes", stocksSousMinimum.size());

        return ResponseEntity.ok(rapport);
    }

    @GetMapping("/clients/top")
    @Operation(summary = "Top clients par chiffre d'affaires")
    public ResponseEntity<List<Map<String, Object>>> getTopClients(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Récupération du top {} clients", limit);

        // Implémentation simplifiée - à compléter avec une vraie requête
        List<Map<String, Object>> topClients = new ArrayList<>();

        return ResponseEntity.ok(topClients);
    }

    @GetMapping("/produits/top")
    @Operation(summary = "Top produits vendus")
    public ResponseEntity<List<Map<String, Object>>> getTopProduits(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Récupération du top {} produits", limit);

        // Implémentation simplifiée
        List<Map<String, Object>> topProduits = new ArrayList<>();

        return ResponseEntity.ok(topProduits);
    }

    @GetMapping("/stocks/valeur")
    @Operation(summary = "Valeur totale du stock")
    public ResponseEntity<Map<String, Object>> getValeurStock() {
        log.info("Calcul de la valeur totale du stock");

        BigDecimal valeurTotale = stockRepository.calculerValeurTotaleStock();

        Map<String, Object> rapport = new HashMap<>();
        rapport.put("valeurTotale", valeurTotale != null ? valeurTotale : BigDecimal.ZERO);
        rapport.put("devise", "EUR");
        rapport.put("dateCalcul", LocalDate.now());

        return ResponseEntity.ok(rapport);
    }
}
