package com.example.account.controller;

import com.example.account.dto.request.OrganizationCreateRequest;
import com.example.account.dto.response.OrganizationResponse;
import com.example.account.model.entity.Organization;
import com.example.account.service.OrganizationService;
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
import java.util.stream.Collectors;

/**
 * REST controller for organization management.
 *
 * Endpoints:
 * - POST /api/organizations/create - Create new organization
 * - GET /api/organizations/{id} - Get organization by ID
 * - GET /api/organizations/user/{userId} - Get user's organizations
 *
 * Note: This controller demonstrates organization management.
 * Authentication integration required for production use.
 */
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organization Management", description = "Endpoints for managing organizations and memberships")
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * Creates a new organization.
     * The creator is automatically assigned as OWNER.
     *
     * @param request organization data
     * @param creatorUserId ID of the user creating the organization (from JWT when auth is implemented)
     * @return created organization
     */
    @PostMapping("/create")
    @Operation(summary = "Create new organization", description = "Creates a new organization and assigns creator as OWNER")
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody OrganizationCreateRequest request,
            @RequestParam UUID creatorUserId) {  // TODO: Extract from JWT token when auth is implemented

        log.info("Creating organization: code={}, creator={}", request.getCode(), creatorUserId);

        Organization organization = mapToEntity(request);
        Organization created = organizationService.createOrganization(organization, creatorUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    /**
     * Gets an organization by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable UUID id) {
        Organization organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(mapToResponse(organization));
    }

    /**
     * Gets all organizations for a user.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's organizations", description = "Retrieves all organizations the user is a member of")
    public ResponseEntity<List<OrganizationResponse>> getUserOrganizations(@PathVariable UUID userId) {
        List<Organization> organizations = organizationService.getUserOrganizations(userId);
        List<OrganizationResponse> responses = organizations.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Mapping methods (consider using MapStruct in production)
    private Organization mapToEntity(OrganizationCreateRequest request) {
        Organization org = new Organization();
        org.setCode(request.getCode());
        org.setShortName(request.getShortName());
        org.setLongName(request.getLongName());
        org.setDescription(request.getDescription());
        org.setLogo(request.getLogo());
        org.setIsActive(true);
        org.setIsIndividual(request.getIsIndividual());
        org.setIsPublic(request.getIsPublic());
        org.setIsBusiness(request.getIsBusiness());
        org.setCountry(request.getCountry());
        org.setCity(request.getCity());
        org.setAddress(request.getAddress());
        org.setLocalization(request.getLocalization());
        org.setOpenTime(request.getOpenTime());
        org.setCloseTime(request.getCloseTime());
        org.setEmail(request.getEmail());
        org.setPhone(request.getPhone());
        org.setWhatsapp(request.getWhatsapp());
        org.setSocialNetworks(request.getSocialNetworks());
        org.setCapitalShare(request.getCapitalShare());
        org.setTaxNumber(request.getTaxNumber());
        return org;
    }

    private OrganizationResponse mapToResponse(Organization org) {
        return OrganizationResponse.builder()
            .id(org.getId())
            .code(org.getCode())
            .shortName(org.getShortName())
            .longName(org.getLongName())
            .description(org.getDescription())
            .logo(org.getLogo())
            .isActive(org.getIsActive())
            .isIndividual(org.getIsIndividual())
            .isPublic(org.getIsPublic())
            .isBusiness(org.getIsBusiness())
            .country(org.getCountry())
            .city(org.getCity())
            .address(org.getAddress())
            .localization(org.getLocalization())
            .openTime(org.getOpenTime())
            .closeTime(org.getCloseTime())
            .email(org.getEmail())
            .phone(org.getPhone())
            .whatsapp(org.getWhatsapp())
            .socialNetworks(org.getSocialNetworks())
            .capitalShare(org.getCapitalShare())
            .taxNumber(org.getTaxNumber())
            .createdAt(org.getCreatedAt())
            .updatedAt(org.getUpdatedAt())
            .build();
    }
}
