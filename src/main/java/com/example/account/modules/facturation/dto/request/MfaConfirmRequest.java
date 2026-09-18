package com.example.account.modules.facturation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

/**
 * Second step of login/try-out once Kernel has replied with mfaRequired=true.
 * tryOut mirrors which entry point the account used to get here (login vs
 * try-out), since Kernel's opaque mfaToken carries no memory of that itself.
 */
@Data
public class MfaConfirmRequest {
    @NotBlank
    private String mfaToken;

    @NotBlank
    private String code;

    private UUID organizationId;

    private boolean tryOut;
}
