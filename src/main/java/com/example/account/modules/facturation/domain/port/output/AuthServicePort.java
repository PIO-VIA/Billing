package com.example.account.modules.facturation.domain.port.output;

import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuthServicePort {
    Mono<SellerAuthResponse> login(String username, String password, UUID organizationId);
    Mono<SellerAuthResponse> tryOut(String principal, String password, UUID organizationId);
    Mono<SellerAuthResponse> confirmMfa(String mfaToken, String code, UUID organizationId);
}
