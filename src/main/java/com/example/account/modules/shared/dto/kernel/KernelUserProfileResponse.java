package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Kernel's GET /api/users/me response — only requires being logged in, unlike
 * GET /api/organizations/my which needs the elevated organizations:read/write
 * permission most employees never have. Carries its own org memberships in
 * `organizations`, which is what login flows actually need.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelUserProfileResponse {
    private UUID id;
    private UUID actorId;
    private String username;
    private String email;
    private List<KernelOrganizationAccessResponse> organizations;
}
