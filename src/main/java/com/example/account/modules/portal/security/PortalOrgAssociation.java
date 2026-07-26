package com.example.account.modules.portal.security;

import java.util.List;
import java.util.UUID;

/** One organization this actor has a customer/supplier record in. */
public record PortalOrgAssociation(UUID organizationId, String organizationName, UUID clientId,
                                    String clientDisplayName, List<String> roles) {
    public boolean hasRole(String role) {
        return roles != null && roles.stream().anyMatch(role::equalsIgnoreCase);
    }
}
