package com.example.account.service;

import com.example.account.context.OrganizationContext;
import com.example.account.model.entity.Organization;
import com.example.account.model.entity.User;
import com.example.account.model.entity.UserOrganization;
import com.example.account.model.enums.OrganizationRole;
import com.example.account.repository.OrganizationRepository;
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
 * Service for managing organizations and user-organization relationships.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new organization and assigns the creator as OWNER.
     */
    @Transactional
    public Organization createOrganization(Organization organization, UUID creatorUserId) {
        log.info("Creating organization: code={}, createdBy={}", organization.getCode(), creatorUserId);

        // Validate unique code
        if (organizationRepository.existsByCode(organization.getCode())) {
            throw new IllegalArgumentException("Organization code already exists: " + organization.getCode());
        }

        // Save organization
        Organization savedOrg = organizationRepository.save(organization);

        // Assign creator as owner
        User creator = userRepository.findById(creatorUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + creatorUserId));

        UserOrganization ownership = new UserOrganization();
        ownership.setUser(creator);
        ownership.setOrganization(savedOrg);
        ownership.setRole(OrganizationRole.OWNER);
        ownership.setIsDefault(true);  // First organization is default
        ownership.setIsActive(true);

        userOrganizationRepository.save(ownership);

        log.info("Organization created successfully: id={}, code={}", savedOrg.getId(), savedOrg.getCode());
        return savedOrg;
    }

    /**
     * Gets an organization by ID.
     */
    public Organization getOrganizationById(UUID organizationId) {
        return organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + organizationId));
    }

    /**
     * Gets all organizations for a user.
     */
    public List<Organization> getUserOrganizations(UUID userId) {
        return organizationRepository.findActiveOrganizationsByUserId(userId);
    }

    /**
     * Adds a user to an organization with a specific role.
     */
    @Transactional
    public UserOrganization addUserToOrganization(UUID userId, UUID organizationId, OrganizationRole role) {
        log.info("Adding user to organization: userId={}, orgId={}, role={}", userId, organizationId, role);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + organizationId));

        // Check if membership already exists
        if (userOrganizationRepository.existsActiveByUserIdAndOrganizationId(userId, organizationId)) {
            throw new IllegalArgumentException("User is already a member of this organization");
        }

        UserOrganization membership = new UserOrganization();
        membership.setUser(user);
        membership.setOrganization(organization);
        membership.setRole(role);
        membership.setIsActive(true);
        membership.setIsDefault(false);

        UserOrganization saved = userOrganizationRepository.save(membership);
        log.info("User added to organization successfully");
        return saved;
    }

    /**
     * Removes a user from an organization (soft delete).
     */
    @Transactional
    public void removeUserFromOrganization(UUID userId, UUID organizationId) {
        log.info("Removing user from organization: userId={}, orgId={}", userId, organizationId);

        UserOrganization membership = userOrganizationRepository
            .findActiveByUserIdAndOrganizationId(userId, organizationId)
            .orElseThrow(() -> new IllegalArgumentException("User is not a member of this organization"));

        // Soft delete
        membership.setIsActive(false);
        membership.setLeftAt(LocalDateTime.now());

        userOrganizationRepository.save(membership);
        log.info("User removed from organization successfully");
    }

    /**
     * Updates user's role in an organization.
     */
    @Transactional
    public void updateUserRole(UUID userId, UUID organizationId, OrganizationRole newRole) {
        log.info("Updating user role: userId={}, orgId={}, newRole={}", userId, organizationId, newRole);

        UserOrganization membership = userOrganizationRepository
            .findActiveByUserIdAndOrganizationId(userId, organizationId)
            .orElseThrow(() -> new IllegalArgumentException("User is not a member of this organization"));

        membership.setRole(newRole);
        userOrganizationRepository.save(membership);

        log.info("User role updated successfully");
    }

    /**
     * Validates user has access to organization.
     */
    public boolean hasUserAccessToOrganization(UUID userId, UUID organizationId) {
        return userOrganizationRepository.existsActiveByUserIdAndOrganizationId(userId, organizationId);
    }

    /**
     * Gets user's role in an organization.
     */
    public OrganizationRole getUserRole(UUID userId, UUID organizationId) {
        return userOrganizationRepository
            .findActiveByUserIdAndOrganizationId(userId, organizationId)
            .map(UserOrganization::getRole)
            .orElse(null);
    }
}
