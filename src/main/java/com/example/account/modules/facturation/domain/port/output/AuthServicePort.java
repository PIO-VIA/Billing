package com.example.account.modules.facturation.domain.port.output;

import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.shared.dto.kernel.KernelRefreshTokenResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuthServicePort {
    Mono<SellerAuthResponse> login(String username, String password, UUID tenantId, UUID organizationId);
    Mono<SellerAuthResponse> tryOut(String principal, String password, UUID tenantId, UUID organizationId);
    Mono<SellerAuthResponse> confirmMfa(String mfaToken, String code, UUID organizationId);

    // Mints a fresh Kernel accessToken (and a rotated refreshToken) from a
    // cached refreshToken — used by PIN login, which has no credentials of
    // its own to log in with directly.
    Mono<KernelRefreshTokenResponse> refreshAccessToken(String refreshToken);
}
