package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.DevisCreateRequest;
import com.example.account.modules.facturation.dto.request.LigneDevisCreateRequest;
import com.example.account.modules.facturation.dto.response.DevisResponse;
import com.example.account.modules.facturation.dto.response.LigneDevisResponse;
import com.example.account.modules.facturation.model.entity.LigneDevis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.repository.LigneDevisRepository;
import com.example.account.modules.facturation.service.DevisService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/devis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Devis", description = "API de gestion des devis")
public class DevisController {

    private final DevisService devisService;

    @PostMapping
    @Operation(summary = "Créer un nouveau devis")
    public ResponseEntity<DevisResponse> createDevis(@Valid @RequestBody DevisCreateRequest request) {
        log.info("Requête de création de devis pour le client: {}", request.getIdClient());
        DevisResponse response = devisService.createDevis(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/ligneDevis/{devisId}")
    public ResponseEntity<LigneDevisResponse> addLigneDevis(@RequestBody LigneDevisCreateRequest request,@PathVariable UUID devisId) {
        
            log.info("adding a new ligne devis  for devis ",devisId);
        
         LigneDevisResponse response=   devisService.addLigneDevis(devisId, request);
         return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{devisId}")
    @Operation(summary = "Mettre à jour un devis")
    public ResponseEntity<DevisResponse> updateDevis(
            @PathVariable UUID devisId,
            @Valid @RequestBody DevisCreateRequest request) {
        log.info("Requête de mise à jour du devis: {}", devisId);
        DevisResponse response = devisService.updateDevis(devisId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{devisId}")
    @Operation(summary = "Récupérer un devis par ID")
    public ResponseEntity<DevisResponse> getDevisById(@PathVariable UUID devisId) {
        log.info("Requête de récupération du devis: {}", devisId);
        DevisResponse response = devisService.getDevisById(devisId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numeroDevis}")
    @Operation(summary = "Récupérer un devis par numéro")
    public ResponseEntity<DevisResponse> getDevisByNumero(@PathVariable String numeroDevis) {
        log.info("Requête de récupération du devis par numéro: {}", numeroDevis);
        DevisResponse response = devisService.getDevisByNumero(numeroDevis);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les devis")
    public ResponseEntity<List<DevisResponse>> getAllDevis() {
        log.info("Requête de récupération de tous les devis");
        List<DevisResponse> responses = devisService.getAllDevis();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Récupérer tous les devis avec pagination")
    public ResponseEntity<Page<DevisResponse>> getAllDevisPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les devis avec pagination");
        Page<DevisResponse> responses = devisService.getAllDevis(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les devis d'un client")
    public ResponseEntity<List<DevisResponse>> getDevisByClient(@PathVariable UUID clientId) {
        log.info("Requête de récupération des devis du client: {}", clientId);
        List<DevisResponse> responses = devisService.getDevisByClient(clientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les devis par statut")
    public ResponseEntity<List<DevisResponse>> getDevisByStatut(@PathVariable StatutDevis statut) {
        log.info("Requête de récupération des devis par statut: {}", statut);
        List<DevisResponse> responses = devisService.getDevisByStatut(statut);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/expires")
    @Operation(summary = "Récupérer les devis expirés")
    public ResponseEntity<List<DevisResponse>> getDevisExpires() {
        log.info("Requête de récupération des devis expirés");
        List<DevisResponse> responses = devisService.getDevisExpires();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les devis par période")
    public ResponseEntity<List<DevisResponse>> getDevisByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Requête de récupération des devis entre {} et {}", dateDebut, dateFin);
        List<DevisResponse> responses = devisService.getDevisByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{devisId}")
    @Operation(summary = "Supprimer un devis")
    public ResponseEntity<Void> deleteDevis(@PathVariable UUID devisId) {
        log.info("Requête de suppression du devis: {}", devisId);
        devisService.deleteDevis(devisId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{devisId}/accepter")
    @Operation(summary = "Accepter un devis")
    public ResponseEntity<DevisResponse> accepterDevis(@PathVariable UUID devisId) {
        log.info("Requête d'acceptation du devis: {}", devisId);
        DevisResponse response = devisService.accepterDevis(devisId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{devisId}/refuser")
    @Operation(summary = "Refuser un devis")
    public ResponseEntity<DevisResponse> refuserDevis(
            @PathVariable UUID devisId,
            @RequestParam(required = false) String motifRefus) {
        log.info("Requête de refus du devis: {}", devisId);
        DevisResponse response = devisService.refuserDevis(devisId, motifRefus);
        return ResponseEntity.ok(response);
    }
}
