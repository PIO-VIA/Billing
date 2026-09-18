package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Only the fields Billing's "Try Out"/portal-auth flows actually need from Kernel's login response. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelLoginResponse {
    private UUID id;
    // Third-party records' partyId is the actor id, not the user id above —
    // needed to resolve which third-party (customer/supplier) a portal login
    // belongs to.
    private UUID actorId;
    private String username;
    private String email;
    // Local seller records (Billing's own sales-core) are sometimes created with
    // whatever contact email the inviter typed in, which can differ from the
    // Kernel account's primary login email — recoveryEmail is the other address
    // Kernel knows for this account, checked as a fallback match.
    private String recoveryEmail;
    private String accessToken;

    // Kernel now requires MFA on every login: instead of accessToken, the
    // response carries these fields and nextStep="CONFIRM_MFA". mfaToken must
    // be echoed back to /api/auth/mfa/confirm along with the OTP code.
    private String nextStep;
    private String mfaToken;
    private String channel;
}
