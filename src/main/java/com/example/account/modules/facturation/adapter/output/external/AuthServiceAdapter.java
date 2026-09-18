package com.example.account.modules.facturation.adapter.output.external;

import com.example.account.modules.core.exception.SalesCoreErrorMapper;
import com.example.account.modules.facturation.domain.port.output.AuthServicePort;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerListItemResponse;
import com.example.account.modules.facturation.model.entity.SellerAgencyAssignment;
import com.example.account.modules.facturation.model.enums.SellerRole;
import com.example.account.modules.facturation.repository.SellerAgencyAssignmentRepository;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelEmployeeMembershipResponse;
import com.example.account.modules.shared.dto.kernel.KernelLoginResponse;
import com.example.account.modules.shared.dto.kernel.KernelOrganizationAccessResponse;
import com.example.account.modules.shared.dto.kernel.KernelOrganizationResponse;
import com.example.account.modules.shared.dto.kernel.KernelUserProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Both regular Sign In and Try Out authenticate directly against Kernel's own
 * /api/auth/login (no separate sales-core seller auth) and then resolve which
 * Kernel organization the session is for. They share the same
 * kernelLoginAndResolveOrganization() flow — Try Out is only distinguished by
 * accepting an organizationId to disambiguate an account that belongs to more
 * than one organization.
 */
@Service
@Slf4j
public class AuthServiceAdapter implements AuthServicePort {

    private final WebClient kernelWebClient;
    private final WebClient salesCoreWebClient;
    private final SellerAgencyAssignmentRepository sellerAgencyAssignmentRepository;

    private static final ParameterizedTypeReference<KernelApiResponse<KernelLoginResponse>> KERNEL_LOGIN_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<KernelUserProfileResponse>> KERNEL_USER_ME_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelOrganizationResponse>>> KERNEL_ORG_LIST_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelEmployeeMembershipResponse>>> KERNEL_EMPLOYEE_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    public AuthServiceAdapter(@Qualifier("kernelWebClient") WebClient kernelWebClient,
                               @Qualifier("salesCoreWebClient") WebClient salesCoreWebClient,
                               SellerAgencyAssignmentRepository sellerAgencyAssignmentRepository) {
        this.kernelWebClient = kernelWebClient;
        this.salesCoreWebClient = salesCoreWebClient;
        this.sellerAgencyAssignmentRepository = sellerAgencyAssignmentRepository;
    }

    @Override
    public Mono<SellerAuthResponse> login(String username, String password, java.util.UUID organizationId) {
        log.info("Authenticating seller '{}' against Kernel", username);
        return kernelLoginAndResolveOrganization(username, password, organizationId);
    }

    @Override
    public Mono<SellerAuthResponse> tryOut(String principal, String password, java.util.UUID organizationId) {
        log.info("Try Out login for principal: {}", principal);
        return kernelLoginAndResolveOrganization(principal, password, organizationId);
    }

    @Override
    public Mono<SellerAuthResponse> confirmMfa(String mfaToken, String code, java.util.UUID organizationId) {
        log.info("Confirming Kernel MFA challenge");
        return kernelWebClient
                .post()
                .uri("/api/auth/mfa/confirm")
                .bodyValue(Map.of("mfaToken", mfaToken, "code", code))
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired MFA code")))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(err -> Mono.error(new ResponseStatusException(
                                        HttpStatus.BAD_GATEWAY, "Kernel MFA confirmation error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(KERNEL_LOGIN_TYPE)
                .map(KernelApiResponse::getData)
                .timeout(Duration.ofSeconds(15))
                .flatMap(kernelUser -> resolveOrganizationForUser(kernelUser, organizationId));
    }

    /**
     * Logs in directly against Kernel, then resolves which of the account's
     * Kernel organizations the session is for:
     * - organizationId given: must be one of the account's orgs (disambiguation from a prior "requires selection" response).
     * - none given, exactly one org: proceed with it directly.
     * - none given, several orgs: don't finish the login yet — hand back the list so the frontend can show a picker.
     * Kernel now requires MFA on every login, so the login call itself never
     * carries an accessToken directly — kernelUser.getNextStep() comes back
     * "CONFIRM_MFA" with a mfaToken instead, which short-circuits straight to
     * the frontend rather than going on to call /api/users/me with no token.
     */
    private Mono<SellerAuthResponse> kernelLoginAndResolveOrganization(String principal, String password, java.util.UUID organizationId) {
        return kernelWebClient
                .post()
                .uri("/api/auth/login")
                .bodyValue(Map.of("principal", principal, "password", password))
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(err -> Mono.error(new ResponseStatusException(
                                        HttpStatus.BAD_GATEWAY, "Kernel auth error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(KERNEL_LOGIN_TYPE)
                .map(KernelApiResponse::getData)
                .timeout(Duration.ofSeconds(15))
                .flatMap(kernelUser -> {
                    if (kernelUser.getAccessToken() == null && "CONFIRM_MFA".equals(kernelUser.getNextStep())) {
                        SellerAuthResponse mfa = new SellerAuthResponse();
                        mfa.setMfaRequired(true);
                        mfa.setMfaToken(kernelUser.getMfaToken());
                        mfa.setMfaChannel(kernelUser.getChannel());
                        return Mono.just(mfa);
                    }
                    return resolveOrganizationForUser(kernelUser, organizationId);
                });
    }

    private Mono<SellerAuthResponse> resolveOrganizationForUser(KernelLoginResponse kernelUser, java.util.UUID organizationId) {
        return kernelWebClient
                .get()
                // /api/organizations/my needs the elevated organizations:read/write
                // permission, which most invited employees (sellers, POS sellers,
                // agency managers) never get assigned — it 403s for anyone but an
                // org admin. /api/users/me only requires being logged in and carries
                // the same org-membership list, sourced from the account's actual
                // employee memberships.
                .uri("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kernelUser.getAccessToken())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(err -> Mono.error(new ResponseStatusException(
                                        HttpStatus.BAD_GATEWAY, "Kernel organizations lookup error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(KERNEL_USER_ME_TYPE)
                .map(KernelApiResponse::getData)
                .timeout(Duration.ofSeconds(15))
                .map(KernelUserProfileResponse::getOrganizations)
                .map(accesses -> accesses == null ? List.<KernelOrganizationResponse>of()
                        : accesses.stream().map(this::toOrganizationResponse).toList())
                .flatMap(orgs -> orgs.isEmpty() ? scanEmployeeMemberships(kernelUser.getActorId()) : Mono.just(orgs))
                .flatMap(orgs -> orgs.isEmpty()
                        ? scanLocalSellersByEmail(List.of(kernelUser.getEmail(), kernelUser.getRecoveryEmail()))
                        : Mono.just(orgs))
                .flatMap(orgs -> resolveOrganization(kernelUser, orgs, organizationId));
    }

    // Same reasoning as PortalIdentityResolver: /api/users/me only reports org
    // memberships the account's own token can see. A freshly-invited employee's
    // Kernel-side membership sometimes isn't reflected there yet (e.g. it was
    // only ever created as a local sales-core seller record), so as a fallback
    // this brute-forces org membership the same way the portal login already
    // does — scanning every org and checking its employee list for a matching
    // actorId. kernelWebClient auto-injects the platform's own service-account
    // token here (no explicit Authorization header set), the same mechanism
    // that lets PortalIdentityResolver's org/third-party scan work regardless
    // of the logged-in account's own permissions. Read-only: finds an existing
    // membership, never creates one.
    private static final int SCAN_CONCURRENCY = 3;

    private Mono<List<KernelOrganizationResponse>> scanEmployeeMemberships(UUID actorId) {
        if (actorId == null) {
            return Mono.just(List.of());
        }
        return listAllOrganizations()
                .flatMap(org -> findEmployeeMembership(actorId, org).map(m -> org), SCAN_CONCURRENCY)
                .collectList();
    }

    private Flux<KernelOrganizationResponse> listAllOrganizations() {
        return kernelWebClient
                .get()
                .uri("/api/organizations")
                .retrieve()
                .bodyToMono(KERNEL_ORG_LIST_TYPE)
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<KernelEmployeeMembershipResponse> findEmployeeMembership(UUID actorId, KernelOrganizationResponse org) {
        return kernelWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/employees").queryParam("organizationId", org.getId()).build())
                .retrieve()
                .bodyToMono(KERNEL_EMPLOYEE_LIST_TYPE)
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable)
                .filter(m -> actorId.equals(m.getActorId()))
                .next()
                .onErrorResume(e -> Mono.empty());
    }

    /**
     * Last-resort fallback: Kernel's own employee-membership records can be
     * missing or broken for a seller (seen live — Kernel's invite endpoint
     * refuses to create a membership for an account it can otherwise
     * authenticate, "EMPLOYEE_NOT_FOUND" on its own email lookup) even though
     * the seller genuinely exists and was assigned an org right here in
     * Billing's own sales-core when they were created (CreateSellerRequest
     * always carries organizationId). So when Kernel has nothing, fall back to
     * Billing's own local seller registry and match by email — this is data
     * Billing itself wrote and fully controls, no dependency on Kernel's
     * membership sync at all.
     */
    private Mono<List<KernelOrganizationResponse>> scanLocalSellersByEmail(List<String> candidateEmails) {
        List<String> emails = candidateEmails.stream().filter(e -> e != null && !e.isBlank()).toList();
        if (emails.isEmpty()) {
            return Mono.just(List.of());
        }
        return listAllOrganizations()
                .flatMap(org -> findLocalSeller(emails, org.getId()).map(s -> org), SCAN_CONCURRENCY)
                .collectList();
    }

    private Mono<SellerListItemResponse> findLocalSeller(List<String> emails, UUID organizationId) {
        return salesCoreWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/sellers/local").queryParam("organizationId", organizationId).build())
                .retrieve()
                .bodyToFlux(SellerListItemResponse.class)
                .filter(s -> emails.stream().anyMatch(email -> email != null && email.equalsIgnoreCase(s.getEmail())))
                .next()
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<SellerAuthResponse> resolveOrganization(KernelLoginResponse kernelUser, List<KernelOrganizationResponse> orgs,
                                                          java.util.UUID organizationId) {
        if (orgs == null || orgs.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No organization found for this account yet."));
        }
        if (organizationId != null) {
            return orgs.stream()
                    .filter(org -> organizationId.equals(org.getId()))
                    .findFirst()
                    .map(org -> mapKernelToSellerAuthResponse(kernelUser, org))
                    .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "This organization is not associated with the account.")));
        }
        if (orgs.size() == 1) {
            return mapKernelToSellerAuthResponse(kernelUser, orgs.get(0));
        }
        SellerAuthResponse selection = new SellerAuthResponse();
        selection.setRequiresOrganizationSelection(true);
        selection.setAvailableOrganizations(orgs);
        return Mono.just(selection);
    }

    /**
     * /api/users/me's org-membership entries don't carry email/taxNumber/logoUri
     * (only /api/organizations/my — the permission-gated endpoint — has those),
     * so those stay null here. Kept as KernelOrganizationResponse so the rest of
     * this adapter and the frontend's org-picker contract don't need to change.
     */
    private KernelOrganizationResponse toOrganizationResponse(KernelOrganizationAccessResponse access) {
        KernelOrganizationResponse org = new KernelOrganizationResponse();
        org.setId(access.getOrganizationId());
        org.setDisplayName(access.getDisplayName() != null ? access.getDisplayName() : access.getShortName());
        org.setLegalName(access.getLegalName());
        org.setShortName(access.getShortName());
        org.setIsActive(true);
        return org;
    }

    private Mono<SellerAuthResponse> mapKernelToSellerAuthResponse(KernelLoginResponse kernelUser, KernelOrganizationResponse org) {
        SellerAuthResponse r = new SellerAuthResponse();
        r.setAccessToken(kernelUser.getAccessToken());
        r.setId(kernelUser.getId());
        r.setUsername(kernelUser.getUsername());
        r.setEmail(kernelUser.getEmail());

        r.setOrganizationId(org.getId());
        r.setOrganizationName(org.getDisplayName() != null ? org.getDisplayName() : org.getShortName());
        r.setOrganizationLogoUri(org.getLogoUri());
        r.setOrganizationEmail(org.getEmail());
        r.setTaxNumber(org.getTaxNumber());
        return enrichWithLocalSeller(r, kernelUser, org.getId());
    }

    /**
     * Kernel's own login response has no seller-specific data at all (role,
     * agency, sale point, permitted sale sizes) — that lives entirely in
     * Billing's own local seller record (sales-core's /api/sellers/local) and,
     * for agency specifically, Billing's own seller_agency_assignments table
     * (see SellerAdminUseCaseImpl.assignAgency — sales-core's own agency field
     * on the seller is never written anymore, since its assign endpoint is
     * broken). This finds the matching local seller by email and layers both
     * on top of the Kernel-derived response. Best-effort: a session-account
     * with no matching local seller record still logs in, just without these
     * extra fields.
     */
    private Mono<SellerAuthResponse> enrichWithLocalSeller(SellerAuthResponse response, KernelLoginResponse kernelUser, UUID organizationId) {
        List<String> emails = List.of(
                kernelUser.getEmail() == null ? "" : kernelUser.getEmail(),
                kernelUser.getRecoveryEmail() == null ? "" : kernelUser.getRecoveryEmail());
        return findLocalSeller(emails, organizationId)
                .flatMap(seller -> {
                    applyLocalSellerFields(response, seller);
                    return sellerAgencyAssignmentRepository.findBySellerId(seller.getId())
                            .doOnNext(assignment -> applyAgencyAssignment(response, assignment))
                            .thenReturn(response);
                })
                .defaultIfEmpty(response)
                .onErrorReturn(response);
    }

    private void applyLocalSellerFields(SellerAuthResponse response, SellerListItemResponse seller) {
        if (seller.getRole() != null) {
            try {
                response.setRole(SellerRole.valueOf(seller.getRole()));
            } catch (IllegalArgumentException ignored) {
                // Unknown/legacy role string — leave role null rather than fail the login.
            }
        }
        response.setSalePoint(seller.getSalePoint());
        response.setSalesPointId(seller.getSalesPointId());
        response.setPermissions(seller.getPermissions());
        response.setPermittedSaleSizes(seller.getPermittedSaleSizes());
        response.setMustChangePassword(seller.getMustChangePassword());
    }

    private void applyAgencyAssignment(SellerAuthResponse response, SellerAgencyAssignment assignment) {
        response.setAgencyId(assignment.getAgencyId());
        response.setAgency(assignment.getAgencyName());
        response.setAgencyEmail(assignment.getAgencyEmail());
        response.setAgencyPhone(assignment.getAgencyPhone());
        response.setAgencyCity(assignment.getAgencyCity());
        response.setAgencyAddress(assignment.getAgencyAddress());
    }
}
