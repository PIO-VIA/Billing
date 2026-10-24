package com.example.account.modules.shared.dto.kernel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Kernel's POST /api/auth/refresh response — rotates both tokens. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KernelRefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String refreshExpiresAt;
}
