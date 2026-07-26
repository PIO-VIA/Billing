package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** POST /api/auth/select-context response. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelContextualLoginResponse {
    private UUID selectedTenantId;
    private UUID selectedOrganizationId;
    private KernelLoginResponse session;
}
