package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/** One selectable identity context from POST /api/auth/discover-contexts. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelLoginContext {
    private String contextId;
    private UUID tenantId;
    private UUID userId;
    private UUID actorId;
    private List<KernelOrganizationAccessResponse> organizations;
}
