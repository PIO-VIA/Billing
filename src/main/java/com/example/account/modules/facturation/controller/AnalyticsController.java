package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.response.ClientChiffreAffaire;
import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.tiers.repository.ClientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public Mono<ResponseEntity<Map<String, Object>>> getRapportVentes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Rapport des ventes entre {} et {}", startDate, endDate);

        Mono<BigDecimal> montantTotalMono = factureRepository.sumMontantByDateBetween(startDate, endDate)
                .defaultIfEmpty(BigDecimal.ZERO);
        Mono<Long> nombreFacturesMono = factureRepository.countByDateBetween(startDate, endDate)
                .defaultIfEmpty(0L);

        return Mono.zip(montantTotalMono, nombreFacturesMono)
                .map(tuple -> {
                    BigDecimal montantTotal = tuple.getT1();
                    Long nombreFactures = tuple.getT2();

                    Map<String, Object> rapport = new HashMap<>();
                    rapport.put("periode", Map.of("debut", startDate, "fin", endDate));
                    rapport.put("montantTotal", montantTotal);
                    rapport.put("nombreFactures", nombreFactures);
                    rapport.put("montantMoyen", nombreFactures > 0
                            ? montantTotal.divide(BigDecimal.valueOf(nombreFactures), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO);
                    return ResponseEntity.ok(rapport);
                });
    }

    @GetMapping("/clients/top")
    @Operation(summary = "Top clients par chiffre d'affaires")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getTopClients(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Récupération du top {} clients", limit);

        return factureRepository.findTopClients(limit)
                .flatMap(cca -> clientRepository.findById(cca.getIdClient())
                        .map(client -> {
                            Map<String, Object> clientData = new HashMap<>();
                            clientData.put("idClient", client.getIdClient());
                            clientData.put("nomComplet", client.getNom() + " " + (client.getPrenom() != null ? client.getPrenom() : ""));
                            clientData.put("totalVentes", cca.getTotalVentes());
                            return clientData;
                        })
                        .defaultIfEmpty(Map.of( // Cas où le client n'est pas trouvé (peu probable mais possible)
                                "idClient", cca.getIdClient(),
                                "nomComplet", "Client Inconnu",
                                "totalVentes", cca.getTotalVentes()
                        ))
                )
                .collectList()
                .map(ResponseEntity::ok);
    }
}
