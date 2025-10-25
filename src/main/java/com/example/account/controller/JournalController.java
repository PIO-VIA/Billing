package com.example.account.controller;

import com.example.account.dto.request.JournalCreateRequest;
import com.example.account.dto.request.JournalUpdateRequest;
import com.example.account.dto.response.JournalResponse;
import com.example.account.service.JournalService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Journal", description = "API de gestion des journaux")
public class JournalController {

    private final JournalService journalService;

    @PostMapping
    @Operation(summary = "Créer un nouveau journal")
    public ResponseEntity<JournalResponse> createJournal(@Valid @RequestBody JournalCreateRequest request) {
        log.info("Requête de création de journal: {}", request.getNomJournal());
        JournalResponse response = journalService.createJournal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{journalId}")
    @Operation(summary = "Mettre à jour un journal")
    public ResponseEntity<JournalResponse> updateJournal(
            @PathVariable UUID journalId,
            @Valid @RequestBody JournalUpdateRequest request) {
        log.info("Requête de mise à jour du journal: {}", journalId);
        JournalResponse response = journalService.updateJournal(journalId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{journalId}")
    @Operation(summary = "Récupérer un journal par ID")
    public ResponseEntity<JournalResponse> getJournalById(@PathVariable UUID journalId) {
        log.info("Requête de récupération du journal: {}", journalId);
        JournalResponse response = journalService.getJournalById(journalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nom/{nomJournal}")
    @Operation(summary = "Récupérer un journal par nom")
    public ResponseEntity<JournalResponse> getJournalByNom(@PathVariable String nomJournal) {
        log.info("Requête de récupération du journal par nom: {}", nomJournal);
        JournalResponse response = journalService.getJournalByNom(nomJournal);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les journals")
    public ResponseEntity<List<JournalResponse>> getAllJournals() {
        log.info("Requête de récupération de tous les journals");
        List<JournalResponse> responses = journalService.getAllJournals();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Récupérer tous les journals avec pagination")
    public ResponseEntity<Page<JournalResponse>> getAllJournalsPaginated(Pageable pageable) {
        log.info("Requête de récupération de tous les journals avec pagination");
        Page<JournalResponse> responses = journalService.getAllJournals(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Récupérer les journals par type")
    public ResponseEntity<List<JournalResponse>> getJournalsByType(@PathVariable String type) {
        log.info("Requête de récupération des journals par type: {}", type);
        List<JournalResponse> responses = journalService.getJournalsByType(type);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des journals par nom")
    public ResponseEntity<List<JournalResponse>> searchJournalsByNom(@RequestParam String nom) {
        log.info("Requête de recherche des journals par nom: {}", nom);
        List<JournalResponse> responses = journalService.searchJournalsByNom(nom);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{journalId}")
    @Operation(summary = "Supprimer un journal")
    public ResponseEntity<Void> deleteJournal(@PathVariable UUID journalId) {
        log.info("Requête de suppression du journal: {}", journalId);
        journalService.deleteJournal(journalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/type/{type}")
    @Operation(summary = "Compter les journals par type")
    public ResponseEntity<Long> countByType(@PathVariable String type) {
        log.info("Requête de comptage des journals par type: {}", type);
        Long count = journalService.countByType(type);
        return ResponseEntity.ok(count);
    }
}
