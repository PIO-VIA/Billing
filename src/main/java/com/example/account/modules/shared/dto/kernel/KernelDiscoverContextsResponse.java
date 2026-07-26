package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** POST /api/auth/discover-contexts response. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelDiscoverContextsResponse {
    private String selectionToken;
    private long expiresInSeconds;
    private List<KernelLoginContext> contexts;
}
