package com.example.account.controller;

import com.example.account.annotation.RequireOrganizationAccess;
import com.example.account.dto.request.SellerCreateRequest;
import com.example.account.dto.request.SellerUpdateRequest;
import com.example.account.dto.response.UpdatedSellerResponse;
import com.example.account.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing sellers (vendeurs).
 * Provides endpoints for creating, updating, and retrieving seller information.
 */
@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Seller", description = "API de gestion des vendeurs")
public class SellerController {

    private final SellerService sellerService;

    /**
     * Create a new seller.
     * Associates a user with an organization in the SELLER role.
     *
     * @param request Seller creation request
     * @return Created seller response
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau vendeur")
    public ResponseEntity<UpdatedSellerResponse> createSeller(@Valid @RequestBody SellerCreateRequest request) {
        log.info("Request to create seller for user {} in organization {}", request.getUserId(), request.getOrganizationId());
        UpdatedSellerResponse response = sellerService.createSeller(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing seller's information.
     *
     * @param sellerId ID of the seller (UserOrganization ID)
     * @param request Update request
     * @return Updated seller response
     */
    @PutMapping("/{sellerId}")
    @Operation(summary = "Mettre à jour un vendeur")
    public ResponseEntity<UpdatedSellerResponse> updateSeller(
            @PathVariable UUID sellerId,
            @Valid @RequestBody SellerUpdateRequest request) {
        log.info("Request to update seller {}", sellerId);
        UpdatedSellerResponse response = sellerService.updateSeller(sellerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get seller information by ID.
     *
     * @param sellerId ID of the seller (UserOrganization ID)
     * @return Seller response
     */
    @GetMapping("/{sellerId}")
    @Operation(summary = "Récupérer les informations d'un vendeur")
    public ResponseEntity<UpdatedSellerResponse> getSellerById(@PathVariable UUID sellerId) {
        log.info("Request to get seller {}", sellerId);
        UpdatedSellerResponse response = sellerService.getSellerById(sellerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all sellers in an organization.
     *
     * @param organizationId Organization ID
     * @return List of sellers
     */
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Récupérer tous les vendeurs d'une organisation")
    @RequireOrganizationAccess
    public ResponseEntity<List<UpdatedSellerResponse>> getSellersByOrganization(@PathVariable UUID organizationId) {
        log.info("Request to get all sellers for organization {}", organizationId);
        List<UpdatedSellerResponse> responses = sellerService.getSellersByOrganization(organizationId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all active sellers in an organization.
     *
     * @param organizationId Organization ID
     * @return List of active sellers
     */
    @GetMapping("/organization/{organizationId}/active")
    @Operation(summary = "Récupérer tous les vendeurs actifs d'une organisation")
    @RequireOrganizationAccess
    public ResponseEntity<List<UpdatedSellerResponse>> getActiveSellersByOrganization(@PathVariable UUID organizationId) {
        log.info("Request to get all active sellers for organization {}", organizationId);
        List<UpdatedSellerResponse> responses = sellerService.getActiveSellersByOrganization(organizationId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Deactivate a seller (soft delete).
     *
     * @param sellerId ID of the seller (UserOrganization ID)
     * @return No content
     */
    @DeleteMapping("/{sellerId}")
    @Operation(summary = "Désactiver un vendeur")
    public ResponseEntity<Void> deactivateSeller(@PathVariable UUID sellerId) {
        log.info("Request to deactivate seller {}", sellerId);
        sellerService.deactivateSeller(sellerId);
        return ResponseEntity.noContent().build();
    }
}
