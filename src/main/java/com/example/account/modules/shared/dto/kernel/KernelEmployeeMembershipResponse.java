package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** One row of GET /api/employees?organizationId=X — only the fields needed to match a membership by actorId. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelEmployeeMembershipResponse {
    private UUID id;
    private UUID organizationId;
    private UUID userId;
    private UUID actorId;
    private String email;
    private String status;
}
