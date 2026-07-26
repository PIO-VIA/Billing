package com.example.account.modules.portal.controller;

import com.example.account.modules.core.exception.SalesCoreErrorMapper;
import com.example.account.modules.portal.dto.PortalLoginRequest;
import com.example.account.modules.portal.dto.PortalLoginResponse;
import com.example.account.modules.portal.dto.PortalOrganizationOption;
import com.example.account.modules.portal.security.PortalIdentityResolver;
import com.example.account.modules.portal.security.PortalOrgAssociation;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelLoginResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Client/fournisseur-portal login — plain Kernel auth (email + password),
 * a person's normal actor account, same identity system as sellers. No
 * discover-contexts/select-context: that's membership-scoped (which orgs is
 * this actor staff of?), useless for a customer/supplier who has a business
 * relationship with an org but was never added as a member of it.
 * <p>
 * Login only needs to authenticate; PortalIdentityResolver then brute-forces
 * every organization this actor has a customer/supplier record in (Kernel
 * has no cross-org third-party search). A person can legitimately have
 * several — there's no "pick one" step, the frontend aggregates documents
 * across all of them and labels each row with its org.
 */
@RestController
@RequestMapping("/api/portal/auth")
public class PortalAuthController {

    private final WebClient kernelWebClient;
    private final PortalIdentityResolver identityResolver;

    private static final ParameterizedTypeReference<KernelApiResponse<KernelLoginResponse>> LOGIN_TYPE =
            new ParameterizedTypeReference<>() {};

    public PortalAuthController(@Qualifier("kernelWebClient") WebClient kernelWebClient, PortalIdentityResolver identityResolver) {
        this.kernelWebClient = kernelWebClient;
        this.identityResolver = identityResolver;
    }

    @PostMapping("/login")
    public Mono<PortalLoginResponse> login(@Valid @RequestBody PortalLoginRequest request) {
        return kernelLogin(request.getPrincipal(), request.getPassword())
                .flatMap(session -> identityResolver.resolveAssociations(session.getActorId())
                        .flatMap(associations -> {
                            if (associations.isEmpty()) {
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "This account has no customer or supplier record in any organization."));
                            }
                            return Mono.just(buildResponse(session, associations));
                        }));
    }

    private Mono<KernelLoginResponse> kernelLogin(String principal, String password) {
        return kernelWebClient
                .post()
                .uri("/api/auth/login")
                .bodyValue(Map.of("principal", principal, "password", password))
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(new ResponseStatusException(
                                        HttpStatus.BAD_GATEWAY, "Kernel auth error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(LOGIN_TYPE)
                .map(KernelApiResponse::getData);
    }

    private PortalLoginResponse buildResponse(KernelLoginResponse session, List<PortalOrgAssociation> associations) {
        PortalLoginResponse response = new PortalLoginResponse();
        response.setAccessToken(session.getAccessToken());
        response.setEmail(session.getEmail());
        response.setName(associations.stream()
                .map(PortalOrgAssociation::clientDisplayName)
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(null));
        response.setOrganizations(associations.stream()
                .map(a -> new PortalOrganizationOption(a.organizationId(), a.organizationName(), a.clientId(), a.roles()))
                .toList());
        return response;
    }
}
