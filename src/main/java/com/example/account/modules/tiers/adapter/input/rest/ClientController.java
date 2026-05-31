package com.example.account.modules.tiers.adapter.input.rest;

import com.example.account.modules.tiers.dto.ClientCreateRequest;
import com.example.account.modules.tiers.dto.ClientUpdateRequest;
import com.example.account.modules.tiers.dto.ClientResponse;
import com.example.account.modules.tiers.domain.port.input.ClientUseCase;
import com.example.account.modules.tiers.domain.model.enums.TypeClient;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client Management", description = "Endpoints for managing clients")
public class ClientController {

    private final ClientUseCase clientUseCase;

    @PostMapping
    @Operation(summary = "Create a new client")
    public Mono<ResponseEntity<ClientResponse>> createClient(@Valid @RequestBody ClientCreateRequest request) {
        return clientUseCase.createClient(request)
                .map(client -> ResponseEntity.status(HttpStatus.CREATED).body(client));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing client")
    public Mono<ResponseEntity<ClientResponse>> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientUpdateRequest request) {
        return clientUseCase.updateClient(id, request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID")
    public Mono<ResponseEntity<ClientResponse>> getClientById(@PathVariable UUID id) {
        return clientUseCase.getClientById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(summary = "Get all clients")
    public Flux<ClientResponse> getAllClients() {
        return clientUseCase.getAllClients();
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active clients")
    public Flux<ClientResponse> getActiveClients() {
        return clientUseCase.getActiveClients();
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get clients by type")
    public Flux<ClientResponse> getClientsByType(@PathVariable TypeClient type) {
        return clientUseCase.getClientsByType(type);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client by ID")
    public Mono<ResponseEntity<Void>> deleteClient(@PathVariable UUID id) {
        return clientUseCase.deleteClient(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
