package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/** One entry in KernelDiscoverContextsResponse.contexts. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelDiscoveredContext {
    private String contextId;
    private UUID tenantId;
    private UUID userId;
    private UUID actorId;
    private String label;
    // SHARED_SPACE / OWN_SPACE / PLATFORM — only SHARED_SPACE/OWN_SPACE contexts
    // carry real organizations; PLATFORM never does.
    private String kind;
    private List<KernelOrganizationAccessResponse> organizations;
}
