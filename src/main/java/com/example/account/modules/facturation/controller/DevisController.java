package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.DevisCreateRequest;
import com.example.account.modules.facturation.dto.response.DevisResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.EnrichedDevisResponse;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.service.DevisService;
import com.example.account.modules.facturation.service.Journals.DevisJournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/devis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Devis", description = "API de gestion des devis (WebFlux)")
public class DevisController {

    private final DevisService devisService;
    private final DevisJournalService devisJournalService;

    @PostMapping
    @Operation(summary = "Créer un nouveau devis")
    public Mono<ResponseEntity<DevisResponse>> createDevis(@Valid @RequestBody DevisCreateRequest request) {
        log.info("Requête de création de devis pour le client: {}", request.getIdClient());
        return devisService.createDevis(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/enriched/{orgId}")
    @Operation(summary = "Enrichir les devis")
    public Flux<EnrichedDevisResponse> getEnrichedDevis(@PathVariable UUID orgId) {
        log.info("Requête de récupération des devis enrichis pour l'organisation: {}", orgId);
        return devisJournalService.enrichDevis(orgId);
    }

    @PutMapping("/{devisId}")
    @Operation(summary = "Mettre à jour un devis")
    public Mono<ResponseEntity<DevisResponse>> updateDevis(
            @PathVariable UUID devisId,
            @Valid @RequestBody DevisCreateRequest request) {
        log.info("Requête de mise à jour du devis: {}", devisId);
        return devisService.updateDevis(devisId, request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{devisId}")
    @Operation(summary = "Récupérer un devis par ID")
    public Mono<ResponseEntity<DevisResponse>> getDevisById(@PathVariable UUID devisId) {
        log.info("Requête de récupération du devis: {}", devisId);
        return devisService.getDevisById(devisId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/numero/{numeroDevis}")
    @Operation(summary = "Récupérer un devis par numéro")
    public Mono<ResponseEntity<DevisResponse>> getDevisByNumero(@PathVariable String numeroDevis) {
        log.info("Requête de récupération du devis par numéro: {}", numeroDevis);
        return devisService.getDevisByNumero(numeroDevis)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les devis")
    public Flux<DevisResponse> getAllDevis() {
        log.info("Requête de récupération de tous les devis");
        return devisService.getAllDevis();
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les devis d'un client")
    public Flux<DevisResponse> getDevisByClient(@PathVariable UUID clientId) {
        log.info("Requête de récupération des devis du client: {}", clientId);
        return devisService.getDevisByClient(clientId);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les devis par statut")
    public Flux<DevisResponse> getDevisByStatut(@PathVariable StatutDevis statut) {
        log.info("Requête de récupération des devis par statut: {}", statut);
        return devisService.getDevisByStatut(statut);
    }

    @GetMapping("/expires")
    @Operation(summary = "Récupérer les devis expirés")
    public Flux<DevisResponse> getDevisExpires() {
        log.info("Requête de récupération des devis expirés");
        return devisService.getDevisExpires();
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les devis par période")
    public Flux<DevisResponse> getDevisByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Requête de récupération des devis entre {} et {}", dateDebut, dateFin);
        return devisService.getDevisByPeriode(dateDebut, dateFin);
    }

    @DeleteMapping("/{devisId}")
    @Operation(summary = "Supprimer un devis")
    public Mono<ResponseEntity<Void>> deleteDevis(@PathVariable UUID devisId) {
        log.info("Requête de suppression du devis: {}", devisId);
        return devisService.deleteDevis(devisId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PutMapping("/{devisId}/accepter")
    @Operation(summary = "Accepter un devis")
    public Mono<ResponseEntity<DevisResponse>> accepterDevis(@PathVariable UUID devisId) {
        log.info("Requête d'acceptation du devis: {}", devisId);
        return devisService.accepterDevis(devisId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{devisId}/refuser")
    @Operation(summary = "Refuser un devis")
    public Mono<ResponseEntity<DevisResponse>> refuserDevis(
            @PathVariable UUID devisId,
            @RequestParam(required = false) String motifRefus) {
        log.info("Requête de refus du devis: {}", devisId);
        return devisService.refuserDevis(devisId, motifRefus)
                .map(ResponseEntity::ok);
    }
}
