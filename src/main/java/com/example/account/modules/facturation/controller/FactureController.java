package com.example.account.modules.facturation.controller;

import com.example.account.modules.facturation.dto.request.FactureCreateRequest;
import com.example.account.modules.facturation.dto.request.FactureUpdateRequest;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.facturation.service.FactureService;
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
@RequestMapping("/api/factures")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Facture", description = "API de gestion des factures")
public class FactureController {

    private final FactureService factureService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle facture")
    public ResponseEntity<FactureResponse> createFacture(@Valid @RequestBody FactureCreateRequest request) {
        log.info("Requête de création de facture pour le client: {}", request.getIdClient());
        FactureResponse response = factureService.createFacture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

  

    @PutMapping("/{factureId}")
    @Operation(summary = "Mettre à jour une facture")
    public ResponseEntity<FactureResponse> updateFacture(
            @PathVariable UUID factureId,
            @Valid @RequestBody FactureUpdateRequest request) {
        log.info("Requête de mise à jour de la facture: {}", factureId);
        FactureResponse response = factureService.updateFacture(factureId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{factureId}")
    @Operation(summary = "Récupérer une facture par ID")
    public ResponseEntity<FactureResponse> getFactureById(@PathVariable UUID factureId) {
        log.info("Requête de récupération de la facture: {}", factureId);
        FactureResponse response = factureService.getFactureById(factureId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numeroFacture}")
    @Operation(summary = "Récupérer une facture par numéro")
    public ResponseEntity<FactureResponse> getFactureByNumero(@PathVariable String numeroFacture) {
        log.info("Requête de récupération de la facture par numéro: {}", numeroFacture);
        FactureResponse response = factureService.getFactureByNumero(numeroFacture);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les factures")
    public ResponseEntity<List<FactureResponse>> getAllFactures() {
        log.info("Requête de récupération de toutes les factures");
        List<FactureResponse> responses = factureService.getAllFactures();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Récupérer toutes les factures avec pagination")
    public ResponseEntity<Page<FactureResponse>> getAllFacturesPaginated(Pageable pageable) {
        log.info("Requête de récupération de toutes les factures avec pagination");
        Page<FactureResponse> responses = factureService.getAllFactures(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les factures d'un client")
    public ResponseEntity<List<FactureResponse>> getFacturesByClient(@PathVariable UUID clientId) {
        log.info("Requête de récupération des factures du client: {}", clientId);
        List<FactureResponse> responses = factureService.getFacturesByClient(clientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Récupérer les factures par état")
    public ResponseEntity<List<FactureResponse>> getFacturesByEtat(@PathVariable StatutFacture etat) {
        log.info("Requête de récupération des factures par état: {}", etat);
        List<FactureResponse> responses = factureService.getFacturesByEtat(etat);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/retard")
    @Operation(summary = "Récupérer les factures en retard")
    public ResponseEntity<List<FactureResponse>> getFacturesEnRetard() {
        log.info("Requête de récupération des factures en retard");
        List<FactureResponse> responses = factureService.getFacturesEnRetard();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/non-payees")
    @Operation(summary = "Récupérer les factures non payées")
    public ResponseEntity<List<FactureResponse>> getFacturesNonPayees() {
        log.info("Requête de récupération des factures non payées");
        List<FactureResponse> responses = factureService.getFacturesNonPayees();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les factures par période")
    public ResponseEntity<List<FactureResponse>> getFacturesByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Requête de récupération des factures entre {} et {}", dateDebut, dateFin);
        List<FactureResponse> responses = factureService.getFacturesByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{factureId}")
    @Operation(summary = "Supprimer une facture")
    public ResponseEntity<Void> deleteFacture(@PathVariable UUID factureId) {
        log.info("Requête de suppression de la facture: {}", factureId);
        factureService.deleteFacture(factureId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{factureId}/marquer-paye")
    @Operation(summary = "Marquer une facture comme payée")
    public ResponseEntity<FactureResponse> marquerCommePaye(@PathVariable UUID factureId) {
        log.info("Requête de marquage de la facture {} comme payée", factureId);
        FactureResponse response = factureService.marquerCommePaye(factureId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{factureId}/paiement")
    @Operation(summary = "Enregistrer un paiement pour une facture")
    public ResponseEntity<FactureResponse> enregistrerPaiement(
            @PathVariable UUID factureId,
            @RequestParam BigDecimal montantPaye) {
        log.info("Requête d'enregistrement d'un paiement de {} pour la facture {}", montantPaye, factureId);
        FactureResponse response = factureService.enregistrerPaiement(factureId, montantPaye);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/etat/{etat}")
    @Operation(summary = "Compter les factures par état")
    public ResponseEntity<Long> countByEtat(@PathVariable StatutFacture etat) {
        log.info("Requête de comptage des factures par état: {}", etat);
        Long count = factureService.countByEtat(etat);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{factureId}/pdf")
    @Operation(summary = "Télécharger le PDF d'une facture")
    public ResponseEntity<byte[]> downloadFacturePdf(@PathVariable UUID factureId) {
        log.info("Requête de téléchargement du PDF de la facture: {}", factureId);

        byte[] pdfBytes = factureService.genererPdfFacture(factureId);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=facture_" + factureId + ".pdf")
                .body(pdfBytes);
    }

    @PostMapping("/{factureId}/envoyer-email")
    @Operation(summary = "Envoyer la facture par email au client")
    public ResponseEntity<Void> envoyerFactureParEmail(@PathVariable UUID factureId) {
        log.info("Requête d'envoi de la facture {} par email", factureId);
        factureService.envoyerFactureParEmail(factureId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{factureId}/rappel-paiement")
    @Operation(summary = "Envoyer un rappel de paiement pour la facture")
    public ResponseEntity<Void> envoyerRappelPaiement(@PathVariable UUID factureId) {
        log.info("Requête d'envoi d'un rappel de paiement pour la facture: {}", factureId);
        factureService.envoyerRappelPaiement(factureId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{factureId}/generer-pdf")
    @Operation(summary = "Générer et sauvegarder le PDF de la facture")
    public ResponseEntity<String> genererEtSauvegarderPdf(@PathVariable UUID factureId) {
        log.info("Requête de génération et sauvegarde du PDF de la facture: {}", factureId);
        String pdfPath = factureService.genererEtSauvegarderPdfFacture(factureId);
        return ResponseEntity.ok(pdfPath);
    }
}
