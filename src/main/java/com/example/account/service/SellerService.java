package com.example.account.service;

import com.example.account.dto.request.SellerCreateRequest;
import com.example.account.dto.request.SellerUpdateRequest;
import com.example.account.dto.response.UpdatedSellerResponse;
import com.example.account.mapper.SellerMapper;
import com.example.account.model.entity.Organization;
import com.example.account.model.entity.User;
import com.example.account.model.entity.UserOrganization;
import com.example.account.model.entity.UserOrganizationPermission;
import com.example.account.model.enums.OrganizationRole;
import com.example.account.model.enums.Permission;
import com.example.account.repository.OrganizationRepository;
import com.example.account.repository.UserOrganizationPermissionRepository;
import com.example.account.repository.UserOrganizationRepository;
import com.example.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing sellers (vendeurs) within organizations.
 * Handles seller creation, updates, permission management, and sale size assignments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserOrganizationPermissionRepository permissionRepository;
    private final SellerMapper sellerMapper;

    /**
     * Create a new seller by associating a user with an organization in the SELLER role.
     *
     * @param request Seller creation request
     * @return Seller response DTO
     * @throws IllegalArgumentException if user or organization not found
     */
    @Transactional
    public UpdatedSellerResponse createSeller(SellerCreateRequest request) {
        log.info("Creating seller for user {} in organization {}", request.getUserId(), request.getOrganizationId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        // Validate organization exists
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + request.getOrganizationId()));

        // Check if user is already a member of this organization
        userOrganizationRepository.findByUserIdAndOrganizationId(user.getId(), organization.getId())
                .ifPresent(uo -> {
                    throw new IllegalArgumentException("User is already a member of this organization");
                });

        // Create UserOrganization with SELLER role
        UserOrganization userOrganization = new UserOrganization();
        userOrganization.setUser(user);
        userOrganization.setOrganization(organization);
        userOrganization.setRole(OrganizationRole.SELLER);
        userOrganization.setAgency(request.getAgency());
        userOrganization.setSalePoint(request.getSalePoint());
        userOrganization.setPermittedSaleSizesList(request.getPermittedSaleSizes());
        userOrganization.setIsActive(true);
        userOrganization.setIsDefault(false);

        UserOrganization savedUserOrganization = userOrganizationRepository.save(userOrganization);

        // Assign permissions
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            for (Permission permission : request.getPermissions()) {
                UserOrganizationPermission uop = new UserOrganizationPermission();
                uop.setUserOrganization(savedUserOrganization);
                uop.setPermission(permission);
                uop.setGrantedBy(null); // TODO: Set to current authenticated user
                permissionRepository.save(uop);
            }
        } else {
            // Assign default seller permissions
            assignDefaultSellerPermissions(savedUserOrganization);
        }

        // Reload to get permissions
        UserOrganization reloadedUserOrganization = userOrganizationRepository.findById(savedUserOrganization.getId())
                .orElseThrow(() -> new IllegalStateException("Failed to reload user organization"));

        log.info("Successfully created seller for user {} in organization {}", user.getUsername(), organization.getShortName());
        return sellerMapper.toSellerResponse(reloadedUserOrganization);
    }

    /**
     * Update an existing seller's information.
     *
     * @param userOrganizationId ID of the UserOrganization record
     * @param request Update request
     * @return Updated seller response
     */
    @Transactional
    public UpdatedSellerResponse updateSeller(UUID userOrganizationId, SellerUpdateRequest request) {
        log.info("Updating seller {}", userOrganizationId);

        UserOrganization userOrganization = userOrganizationRepository.findById(userOrganizationId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + userOrganizationId));

        // Validate that this is actually a seller
        if (userOrganization.getRole() != OrganizationRole.SELLER) {
            throw new IllegalArgumentException("User organization is not a seller");
        }

        // Update fields if provided
        if (request.getAgency() != null) {
            userOrganization.setAgency(request.getAgency());
        }

        if (request.getSalePoint() != null) {
            userOrganization.setSalePoint(request.getSalePoint());
        }

        if (request.getPermittedSaleSizes() != null) {
            userOrganization.setPermittedSaleSizesList(request.getPermittedSaleSizes());
        }

        if (request.getIsActive() != null) {
            userOrganization.setIsActive(request.getIsActive());
            if (!request.getIsActive()) {
                userOrganization.setLeftAt(LocalDateTime.now());
            }
        }

        // Update permissions if provided
        if (request.getPermissions() != null) {
            // Remove existing permissions
            permissionRepository.deleteByUserOrganizationId(userOrganizationId);

            // Add new permissions
           for (Permission permission : request.getPermissions()) {
                UserOrganizationPermission uop = new UserOrganizationPermission();
                uop.setUserOrganization(userOrganization);
                uop.setPermission(permission);
                uop.setGrantedBy(null); // TODO: Set to current authenticated user
                permissionRepository.save(uop);
            }
        }

        UserOrganization updatedUserOrganization = userOrganizationRepository.save(userOrganization);

        log.info("Successfully updated seller {}", userOrganizationId);
        return sellerMapper.toSellerResponse(updatedUserOrganization);
    }

    /**
     * Get seller information by UserOrganization ID.
     *
     * @param userOrganizationId ID of the UserOrganization record
     * @return Seller response DTO
     */
    @Transactional(readOnly = true)
    public UpdatedSellerResponse getSellerById(UUID userOrganizationId) {
        log.info("Retrieving seller information for {}", userOrganizationId);

        UserOrganization userOrganization = userOrganizationRepository.findById(userOrganizationId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + userOrganizationId));

        if (userOrganization.getRole() != OrganizationRole.SELLER) {
            throw new IllegalArgumentException("User organization is not a seller");
        }

        return sellerMapper.toSellerResponse(userOrganization);
    }

    /**
     * Get all sellers in an organization.
     *
     * @param organizationId Organization ID
     * @return List of seller response DTOs
     */
    @Transactional(readOnly = true)
    public List<UpdatedSellerResponse> getSellersByOrganization(UUID organizationId) {
        log.info("Retrieving all sellers for organization {}", organizationId);

        List<UserOrganization> sellers = userOrganizationRepository.findByOrganizationIdAndRole(
                organizationId,
                OrganizationRole.SELLER
        );

        return sellerMapper.toSellerResponseList(sellers);
    }

    /**
     * Get all active sellers in an organization.
     *
     * @param organizationId Organization ID
     * @return List of active seller response DTOs
     */
    @Transactional(readOnly = true)
    public List<UpdatedSellerResponse> getActiveSellersByOrganization(UUID organizationId) {
        log.info("Retrieving all active sellers for organization {}", organizationId);

        List<UserOrganization> sellers = userOrganizationRepository.findByOrganizationIdAndRole(
                organizationId,
                OrganizationRole.SELLER
        ).stream()
                .filter(UserOrganization::isActiveMembership)
                .toList();

        return sellerMapper.toSellerResponseList(sellers);
    }

    /**
     * Deactivate a seller (soft delete).
     *
     * @param userOrganizationId ID of the UserOrganization record
     */
    @Transactional
    public void deactivateSeller(UUID userOrganizationId) {
        log.info("Deactivating seller {}", userOrganizationId);

        UserOrganization userOrganization = userOrganizationRepository.findById(userOrganizationId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + userOrganizationId));

        if (userOrganization.getRole() != OrganizationRole.SELLER) {
            throw new IllegalArgumentException("User organization is not a seller");
        }

        userOrganization.setIsActive(false);
        userOrganization.setLeftAt(LocalDateTime.now());
        userOrganizationRepository.save(userOrganization);

        log.info("Successfully deactivated seller {}", userOrganizationId);
    }

    /**
     * Assign default permissions to a new seller.
     *
     * @param userOrganization The seller's user-organization record
     */
    private void assignDefaultSellerPermissions(UserOrganization userOrganization) {
        log.debug("Assigning default seller permissions to {}", userOrganization.getId());

        // Default seller permissions
        List<Permission> defaultPermissions = List.of(
                Permission.CREATE_INVOICE,
                Permission.EDIT_INVOICE,
                Permission.SEND_INVOICE,
                Permission.CREATE_QUOTE,
                Permission.EDIT_QUOTE,
                Permission.CONVERT_QUOTE,
                Permission.RECORD_PAYMENT,
                Permission.CREATE_DELIVERY,
                Permission.MANAGE_CUSTOMERS
        );

        for (Permission permission : defaultPermissions) {
            UserOrganizationPermission uop = new UserOrganizationPermission();
            uop.setUserOrganization(userOrganization);
            uop.setPermission(permission);
            permissionRepository.save(uop);
        }
    }
}
