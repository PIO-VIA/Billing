package com.example.account.modules.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A client/fournisseur account can have a customer/supplier record in more
 * than one organization (a third-party record's id is per-organization,
 * Kernel has no cross-org concept) — documents are aggregated across all of
 * them rather than scoped to one "selected" org, so this always returns the
 * full list, never a single organizationId/clientId to pick between.
 */
@Data
@NoArgsConstructor
public class PortalLoginResponse {

    // Kernel's own access token — used as-is as the portal bearer token,
    // there's no separate portal-issued JWT.
    private String accessToken;

    private String email;
    private String name;

    private List<PortalOrganizationOption> organizations;
}
