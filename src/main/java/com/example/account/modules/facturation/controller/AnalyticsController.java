package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.tiers.repository.ClientRepository;
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

    @GetMapping("/clients/top")
    @Operation(summary = "Top clients par chiffre d'affaires")
    public ResponseEntity<List<Map<String, Object>>> getTopClients(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Récupération du top {} clients", limit);

        // Implémentation simplifiée - à compléter avec une vraie requête
        List<Map<String, Object>> topClients = new ArrayList<>();

        return ResponseEntity.ok(topClients);
    }

    // TODO: Module Stock – will be implemented later
    /*
    @GetMapping("/stocks/alertes")
    ...
    @GetMapping("/produits/top")
    ...
    @GetMapping("/stocks/valeur")
    ...
    */
}
