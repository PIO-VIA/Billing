package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.response.TableauDeBordResponse;
import com.example.account.modules.facturation.service.TableauDeBordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tableau-de-bord")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tableau de Bord", description = "API du tableau de bord et KPIs (WebFlux)")
public class TableauDeBordController {

    private final TableauDeBordService tableauDeBordService;

    @GetMapping
    @Operation(summary = "Obtenir le tableau de bord complet")
    public Mono<ResponseEntity<TableauDeBordResponse>> getTableauDeBord() {
        log.info("Requête de récupération du tableau de bord");
        return tableauDeBordService.getTableauDeBord()
                .map(ResponseEntity::ok);
    }
}
