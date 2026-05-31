package com.example.account.modules.tiers.adapter.input.rest;

import com.example.account.modules.tiers.dto.FournisseurCreateRequest;
import com.example.account.modules.tiers.dto.FournisseurUpdateRequest;
import com.example.account.modules.tiers.dto.FournisseurResponse;
import com.example.account.modules.tiers.domain.port.input.FournisseurUseCase;
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
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Tag(name = "Fournisseur Management", description = "Endpoints for managing fournisseurs")
public class FournisseurController {

    private final FournisseurUseCase fournisseurUseCase;

    @PostMapping
    @Operation(summary = "Create a new fournisseur")
    public Mono<ResponseEntity<FournisseurResponse>> createFournisseur(@Valid @RequestBody FournisseurCreateRequest request) {
        return fournisseurUseCase.createFournisseur(request)
                .map(fournisseur -> ResponseEntity.status(HttpStatus.CREATED).body(fournisseur));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing fournisseur")
    public Mono<ResponseEntity<FournisseurResponse>> updateFournisseur(@PathVariable UUID id, @Valid @RequestBody FournisseurUpdateRequest request) {
        return fournisseurUseCase.updateFournisseur(id, request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fournisseur by ID")
    public Mono<ResponseEntity<FournisseurResponse>> getFournisseurById(@PathVariable UUID id) {
        return fournisseurUseCase.getFournisseurById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(summary = "Get all fournisseurs")
    public Flux<FournisseurResponse> getAllFournisseurs() {
        return fournisseurUseCase.getAllFournisseurs();
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active fournisseurs")
    public Flux<FournisseurResponse> getActiveFournisseurs() {
        return fournisseurUseCase.getActiveFournisseurs();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete fournisseur by ID")
    public Mono<ResponseEntity<Void>> deleteFournisseur(@PathVariable UUID id) {
        return fournisseurUseCase.deleteFournisseur(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
