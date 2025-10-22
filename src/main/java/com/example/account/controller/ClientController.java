package com.example.account.controller;

import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.request.ClientUpdateRequest;
import com.example.account.dto.response.ClientResponse;
import com.example.account.model.enums.TypeClient;
import com.example.account.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client", description = "API de gestion des clients")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Créer un nouveau client")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
        log.info("Requête de création de client: {}", request.getUsername());
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{clientId}")
    @Operation(summary = "Mettre à jour un client")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable UUID clientId,
            @Valid @RequestBody ClientUpdateRequest request) {
        log.info("Requête de mise à jour du client: {}", clientId);
        ClientResponse response = clientService.updateClient(clientId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}")
    @Operation(summary = "Récupérer un client par ID")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable UUID clientId) {
        log.info("Requête de récupération du client: {}", clientId);
        ClientResponse response = clientService.getClientById(clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Récupérer un client par username")
    public ResponseEntity<ClientResponse> getClientByUsername(@PathVariable String username) {
        log.info("Requête de récupération du client par username: {}", username);
        ClientResponse response = clientService.getClientByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les clients")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        log.info("Requête de récupération de tous les clients");
        List<ClientResponse> responses = clientService.getAllClients();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/actifs")
    @Operation(summary = "Récupérer tous les clients actifs")
    public ResponseEntity<List<ClientResponse>> getActiveClients() {
        log.info("Requête de récupération des clients actifs");
        List<ClientResponse> responses = clientService.getActiveClients();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{typeClient}")
    @Operation(summary = "Récupérer les clients par type")
    public ResponseEntity<List<ClientResponse>> getClientsByType(@PathVariable TypeClient typeClient) {
        log.info("Requête de récupération des clients par type: {}", typeClient);
        List<ClientResponse> responses = clientService.getClientsByType(typeClient);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{clientId}")
    @Operation(summary = "Supprimer un client")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID clientId) {
        log.info("Requête de suppression du client: {}", clientId);
        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{clientId}/solde")
    @Operation(summary = "Mettre à jour le solde d'un client")
    public ResponseEntity<ClientResponse> updateSolde(
            @PathVariable UUID clientId,
            @RequestParam Double montant) {
        log.info("Requête de mise à jour du solde du client {}: {}", clientId, montant);
        ClientResponse response = clientService.updateSolde(clientId, montant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/actifs")
    @Operation(summary = "Compter les clients actifs")
    public ResponseEntity<Long> countActiveClients() {
        log.info("Requête de comptage des clients actifs");
        Long count = clientService.countActiveClients();
        return ResponseEntity.ok(count);
    }
}
