package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.PaiementCreateRequest;
import com.example.account.modules.facturation.dto.request.PaiementUpdateRequest;
import com.example.account.modules.facturation.dto.response.PaiementResponse;
import com.example.account.modules.facturation.service.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/paiement")
@RequiredArgsConstructor
@Tag(name = "Paiement")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement")
    public Mono<ResponseEntity<PaiementResponse>> createPaiement(@Valid @RequestBody PaiementCreateRequest request) {
        return paiementService.createPaiement(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un paiement")
    public Mono<ResponseEntity<PaiementResponse>> updatePaiement(
            @PathVariable UUID id,
            @Valid @RequestBody PaiementUpdateRequest request) {
        return paiementService.updatePaiement(id, request)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(summary = "Get all paiements")
    public Flux<PaiementResponse> getAllPaiements() {
        return paiementService.getAllPaiements();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get paiement by ID")
    public Mono<ResponseEntity<PaiementResponse>> getPaiementById(@PathVariable UUID id) {
        return paiementService.getPaiementById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les paiements d'un client")
    public Flux<PaiementResponse> getPaiementsByClient(@PathVariable UUID clientId) {
        return paiementService.getPaiementsByClient(clientId);
    }

    @GetMapping("/facture/{factureId}")
    @Operation(summary = "Récupérer les paiements d'une facture")
    public Flux<PaiementResponse> getPaiementsByFacture(@PathVariable UUID factureId) {
        return paiementService.getPaiementsByFacture(factureId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete paiement")
    public Mono<ResponseEntity<Void>> deletePaiement(@PathVariable UUID id) {
        return paiementService.deletePaiement(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
