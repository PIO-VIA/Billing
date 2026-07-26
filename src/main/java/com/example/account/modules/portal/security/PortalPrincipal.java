package com.example.account.modules.portal.security;

import java.util.List;
import java.util.UUID;

/**
 * A resolved client/fournisseur-portal identity: every organization this
 * Kernel actor has a customer/supplier record in, each with its own
 * clientId (a third-party record's id is per-organization) and roles.
 * Documents are aggregated across ALL of these — no single "selected org"
 * anymore — the frontend shows which org each row came from.
 * Not decoded from a token — Kernel's own JWT is what's actually used as
 * the bearer token now, so this is re-resolved per request (see
 * PortalIdentityResolver).
 */
public record PortalPrincipal(List<PortalOrgAssociation> associations, String email, String name) {

    public static PortalPrincipal of(List<PortalOrgAssociation> associations, String email) {
        String name = associations.stream()
                .map(PortalOrgAssociation::clientDisplayName)
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(null);
        return new PortalPrincipal(associations, email, name);
    }

    public boolean hasRole(String role) {
        return associations.stream().anyMatch(a -> a.hasRole(role));
    }

    public List<PortalOrgAssociation> associationsWithRole(String role) {
        return associations.stream().filter(a -> a.hasRole(role)).toList();
    }

    /** The clientId for a specific org, if this actor has one there. */
    public UUID clientIdFor(UUID organizationId) {
        return associations.stream()
                .filter(a -> a.organizationId().equals(organizationId))
                .map(PortalOrgAssociation::clientId)
                .findFirst()
                .orElse(null);
    }
}
