package com.example.account.modules.portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Just email + password — the org is resolved via Kernel's own discover-contexts, not supplied by the caller. */
@Data
public class PortalLoginRequest {

    @NotBlank
    private String principal;

    @NotBlank
    private String password;
}
