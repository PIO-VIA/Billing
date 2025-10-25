package com.example.account.controller;

import com.example.account.dto.response.TableauDeBordResponse;
import com.example.account.service.TableauDeBordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tableau-de-bord")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tableau de Bord", description = "API du tableau de bord et KPIs")
public class TableauDeBordController {

    private final TableauDeBordService tableauDeBordService;

    @GetMapping
    @Operation(summary = "Obtenir le tableau de bord complet")
    public ResponseEntity<TableauDeBordResponse> getTableauDeBord() {
        log.info("Requête de récupération du tableau de bord");
        TableauDeBordResponse response = tableauDeBordService.getTableauDeBord();
        return ResponseEntity.ok(response);
    }
}
