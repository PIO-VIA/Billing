package com.example.account.modules.facturation.application.usecase.impl;

import com.example.account.modules.facturation.domain.port.input.AuthUseCase;
import com.example.account.modules.facturation.domain.port.output.AuthServicePort;
import com.example.account.modules.facturation.domain.port.output.SellerServicePort;
import com.example.account.modules.facturation.dto.request.CreateSellerRequest;
import com.example.account.modules.facturation.dto.request.LoginRequest;
import com.example.account.modules.facturation.dto.request.MfaConfirmRequest;
import com.example.account.modules.facturation.dto.request.PinLoginRequest;
import com.example.account.modules.facturation.dto.request.SellerUIPermissionsRequest;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerListItemResponse;
import com.example.account.modules.facturation.model.entity.SellerAuthContext;
import com.example.account.modules.facturation.model.enums.SaleSize;
import com.example.account.modules.facturation.model.enums.SellerPermission;
import com.example.account.modules.facturation.model.enums.SellerRole;
import com.example.account.modules.facturation.repository.SellerAuthContextRepository;
import com.example.account.modules.settings.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUseCaseImpl implements AuthUseCase {

    private final AuthServicePort authServicePort;
    private final SellerServicePort sellerServicePort;
    private final SellerAuthContextRepository sellerAuthContextRepository;
    private final SettingService settingService;

    @Override
    public Mono<SellerAuthResponse> login(LoginRequest request) {
        return authServicePort.login(request.getUsername(), request.getPassword(), request.getTenantId(), request.getOrganizationId())
                .flatMap(this::attachExistingSeller)
                .flatMap(this::cacheKernelSession)
                .flatMap(this::withOrganizationSettings);
    }

    @Override
    public Mono<SellerAuthResponse> tryOut(LoginRequest request) {
        return authServicePort.tryOut(request.getUsername(), request.getPassword(), request.getTenantId(), request.getOrganizationId())
                .flatMap(this::ensureSellerProvisioned)
                .flatMap(this::cacheKernelSession)
                .flatMap(this::withOrganizationSettings);
    }

    /**
     * Second step once login()/tryOut() came back with mfaRequired=true.
     * request.tryOut() picks up the same branching login()/tryOut() use —
     * attach an existing seller for Sign In, auto-provision an OWNER seller
     * for Try Out — since Kernel's mfaToken itself carries no memory of which
     * entry point the account originally used.
     */
    @Override
    public Mono<SellerAuthResponse> confirmMfa(MfaConfirmRequest request) {
        Mono<SellerAuthResponse> resolved = authServicePort.confirmMfa(
                request.getMfaToken(), request.getCode(), request.getOrganizationId());
        return (request.isTryOut()
                ? resolved.flatMap(this::ensureSellerProvisioned)
                : resolved.flatMap(this::attachExistingSeller))
                .flatMap(this::cacheKernelSession)
                .flatMap(this::withOrganizationSettings);
    }

    /**
     * Caches the Kernel refreshToken this login/tryOut/confirmMfa call just
     * received, keyed by the seller resolved above, so a later PIN login (see
     * loginByLocalPin) can mint a fresh accessToken instead of having no
     * Kernel session at all. Only sellers with an existing local PIN context
     * get one cached — a response with no resolved seller id (e.g. the
     * requiresOrganizationSelection branch) or no refreshToken (Kernel didn't
     * issue one) is left as-is; best-effort, never fails the login itself.
     */
    private Mono<SellerAuthResponse> cacheKernelSession(SellerAuthResponse response) {
        if (response.getId() == null || response.getRefreshToken() == null) {
            return Mono.just(response);
        }
        return sellerAuthContextRepository.findBySellerId(response.getId())
                .flatMap(context -> {
                    context.setRefreshToken(response.getRefreshToken());
                    context.setRefreshExpiresAt(parseIsoDateTime(response.getRefreshExpiresAt()));
                    context.setUpdatedAt(java.time.LocalDateTime.now());
                    return sellerAuthContextRepository.save(context);
                })
                .thenReturn(response)
                .onErrorReturn(response);
    }

    private java.time.LocalDateTime parseIsoDateTime(String isoDateTime) {
        if (isoDateTime == null) {
            return null;
        }
        try {
            return java.time.OffsetDateTime.parse(isoDateTime).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sign In only ever authenticated a person who's already a known seller —
     * if one now exists for this Kernel account (e.g. provisioned by an
     * earlier Try Out, or invited normally), reflect its real role and
     * permissions instead of leaving them null. Doesn't create one: an
     * account with no seller record just gets Kernel's bare login data,
     * same as before.
     */
    private Mono<SellerAuthResponse> attachExistingSeller(SellerAuthResponse response) {
        return findExistingSeller(response)
                .flatMap(existing -> applyExistingSeller(response, existing))
                .defaultIfEmpty(response);
    }

    /**
     * Try Out authenticates against Kernel directly, so there's no local seller
     * record backing the session yet. On first use for a given (org, username),
     * auto-provision one as OWNER with every permission granted — this account
     * already owns the Kernel organization, so it should walk in with full
     * access rather than the empty-permission default a brand-new seller gets.
     * An existing seller (from a prior Try Out or a normal invite) is left as-is
     * and just mapped onto the response, so this never silently re-grants access
     * an admin deliberately restricted later.
     */
    private Mono<SellerAuthResponse> ensureSellerProvisioned(SellerAuthResponse response) {
        if (Boolean.TRUE.equals(response.getRequiresOrganizationSelection()) || response.getOrganizationId() == null) {
            return Mono.just(response);
        }
        return findExistingSeller(response)
                .flatMap(existing -> applyExistingSeller(response, existing))
                .switchIfEmpty(Mono.defer(() -> provisionOwnerSeller(response)));
    }

    private Mono<SellerListItemResponse> findExistingSeller(SellerAuthResponse response) {
        if (Boolean.TRUE.equals(response.getRequiresOrganizationSelection()) || response.getOrganizationId() == null) {
            return Mono.empty();
        }
        // Matches by email, not username — Kernel's own account username
        // (e.g. "furehmitoto") and the local seller record's username
        // (freely chosen at seller-creation time, e.g. "fureh-pos") have no
        // reason to agree. AuthServiceAdapter.enrichWithLocalSeller already
        // matches local sellers by email for the same reason; matching here
        // on username instead of email meant this lookup silently failed to
        // find a seller that enrichWithLocalSeller had already found,
        // leaving response.getId() as Kernel's own user id instead of the
        // local seller's — which then broke anything keyed off that id,
        // e.g. caching the Kernel refresh token for PIN login.
        return sellerServicePort.listSellers(response.getOrganizationId())
                .filter(seller -> response.getEmail() != null && response.getEmail().equalsIgnoreCase(seller.getEmail()))
                .next();
    }

    private Mono<SellerAuthResponse> applyExistingSeller(SellerAuthResponse response, SellerListItemResponse seller) {
        response.setId(seller.getId());
        response.setRole(seller.getRole() != null ? SellerRole.valueOf(seller.getRole()) : null);
        response.setAgency(seller.getAgency());
        response.setSalePoint(seller.getSalePoint());
        response.setPermissions(seller.getPermissions());
        response.setPermittedSaleSizes(seller.getPermittedSaleSizes());
        response.setAgencyId(seller.getAgencyId());
        response.setSalesPointId(seller.getSalesPointId());
        response.setMustChangePassword(seller.getMustChangePassword());
        return sellerServicePort.getUIPermissions(seller.getId())
                .doOnNext(response::setUiPermissions)
                .thenReturn(response)
                .onErrorReturn(response);
    }

    private Mono<SellerAuthResponse> provisionOwnerSeller(SellerAuthResponse response) {
        log.info("Try Out: auto-provisioning OWNER seller '{}' for organization {}",
                response.getUsername(), response.getOrganizationId());

        CreateSellerRequest request = new CreateSellerRequest();
        request.setUsername(response.getUsername());
        request.setEmail(response.getEmail());
        // Kernel's login response has no first/last name split — username is
        // the only identity Try Out actually has available at this point.
        request.setFirstName(response.getUsername());
        request.setLastName("Owner");
        request.setRole(SellerRole.OWNER.name());
        request.setOrganizationId(response.getOrganizationId());
        request.setOrganizationName(response.getOrganizationName());
        request.setOrganizationLogoUri(response.getOrganizationLogoUri());
        request.setOrganizationEmail(response.getOrganizationEmail());
        request.setTaxNumber(response.getTaxNumber());
        request.setPermissions(List.of(SellerPermission.values()).stream().map(Enum::name).toList());
        request.setPermittedSaleSizes(List.of(SaleSize.values()).stream().map(Enum::name).toList());

        return sellerServicePort.createSeller(request)
                .flatMap(created -> {
                    response.setId(created.getId());
                    response.setRole(SellerRole.valueOf(created.getRole()));
                    response.setPermissions(List.of(SellerPermission.values()));
                    response.setPermittedSaleSizes(List.of(SaleSize.values()));
                    return sellerServicePort.setUIPermissions(created.getId(), fullUiPermissions())
                            .doOnNext(response::setUiPermissions)
                            .thenReturn(response)
                            .onErrorReturn(response);
                });
    }

    private SellerUIPermissionsRequest fullUiPermissions() {
        SellerUIPermissionsRequest p = new SellerUIPermissionsRequest();
        p.setSectionSalesManagement(true);
        p.setSalesQuotations(true);
        p.setSalesProformaInvoice(true);
        p.setSalesSalesOrders(true);
        p.setSalesInvoices(true);
        p.setSalesDeliveryNote(true);
        p.setSalesCreditNotes(true);
        p.setSalesBackOrders(true);
        p.setSectionPurchasingLogistics(true);
        p.setPurchasingPurchaseOrder(true);
        p.setPurchasingGoodsReceiptNote(true);
        p.setPurchasingSupplierInvoice(true);
        p.setSectionAccountingJournals(true);
        p.setJournalsQuotation(true);
        p.setJournalsSaleOrder(true);
        p.setJournalsPurchaseOrder(true);
        p.setJournalsClientInvoice(true);
        p.setJournalsSupplierInvoice(true);
        p.setSectionOrganization(true);
        p.setOrganizationAgencies(true);
        p.setOrganizationSellers(true);
        p.setOrganizationCustomers(true);
        p.setOrganizationSuppliers(true);
        p.setOrganizationSalePoints(true);
        p.setOrganizationSessions(true);
        p.setOrganizationProducts(true);
        p.setSectionSettings(true);
        p.setSettingsPreferences(true);
        return p;
    }

    /**
     * Identifies a seller from Billing's own local PIN store (SellerAuthContext)
     * instead of sales-core's PIN login. Fetches the matching seller by listing
     * the org's sellers and filtering client-side — a stand-in for a direct
     * get-by-id call until sales-core's GET /api/sellers/{sellerId} (added
     * locally, not yet deployed) is reachable from here.
     *
     * PIN login never talks to Kernel with credentials of its own, so without
     * this it would hand the frontend a SellerAuthResponse with no
     * accessToken — every subsequent Kernel-bound request would then go out
     * with no caller identity at all. Mints a fresh accessToken from the
     * refreshToken cached the last time this seller logged in normally (see
     * cacheKernelSession); a seller who never has, or whose cached refresh
     * token has since expired, gets a clear 401 instead.
     */
    @Override
    public Mono<SellerAuthResponse> loginByLocalPin(PinLoginRequest request) {
        return sellerAuthContextRepository.findByOrganizationIdAndPin(request.getOrganizationId(), request.getPin())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid PIN")))
                .flatMap(context -> {
                    if (context.getRefreshToken() == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "PIN login requires signing in normally at least once first."));
                    }
                    return sellerServicePort.listSellers(context.getOrganizationId())
                            .filter(seller -> context.getSellerId().equals(seller.getId()))
                            .next()
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Seller not found: " + context.getSellerId())))
                            .map(this::mapToSellerAuthResponse)
                            .flatMap(response -> attachRefreshedKernelSession(response, context));
                })
                .flatMap(this::withOrganizationSettings);
    }

    /**
     * Refresh tokens are typically single-use (Kernel rotates them on every
     * refresh), so the rotated one Kernel hands back here is persisted
     * immediately — reusing the old one on the next PIN login would fail.
     */
    private Mono<SellerAuthResponse> attachRefreshedKernelSession(SellerAuthResponse response, SellerAuthContext context) {
        return authServicePort.refreshAccessToken(context.getRefreshToken())
                .flatMap(refreshed -> {
                    response.setAccessToken(refreshed.getAccessToken());
                    context.setRefreshToken(refreshed.getRefreshToken());
                    context.setRefreshExpiresAt(parseIsoDateTime(refreshed.getRefreshExpiresAt()));
                    context.setUpdatedAt(java.time.LocalDateTime.now());
                    return sellerAuthContextRepository.save(context).thenReturn(response);
                });
    }

    private SellerAuthResponse mapToSellerAuthResponse(SellerListItemResponse seller) {
        SellerAuthResponse r = new SellerAuthResponse();
        r.setId(seller.getId());
        r.setUsername(seller.getUsername());
        r.setRole(seller.getRole() != null ? SellerRole.valueOf(seller.getRole()) : null);
        r.setAgency(seller.getAgency());
        r.setSalePoint(seller.getSalePoint());
        r.setPermissions(seller.getPermissions());
        r.setPermittedSaleSizes(seller.getPermittedSaleSizes());
        r.setOrganizationId(seller.getOrganizationId());
        r.setAgencyId(seller.getAgencyId());
        r.setSalesPointId(seller.getSalesPointId());
        r.setMustChangePassword(seller.getMustChangePassword());
        r.setCreatedAt(seller.getCreatedAt());
        return r;
    }

    private Mono<SellerAuthResponse> withOrganizationSettings(SellerAuthResponse response) {
        if (response.getOrganizationId() == null) {
            return Mono.just(response);
        }
        return Mono.zip(
                        settingService.getOrganizationSettings(response.getOrganizationId()),
                        settingService.listSequenceSettings(response.getOrganizationId()).collectList()
                )
                .map(tuple -> {
                    response.setOrganizationSettings(tuple.getT1());
                    response.setDocumentNumberingSettings(tuple.getT2());
                    // organizationLogoUri is a snapshot taken at seller-creation
                    // time and never updated — every document preview reads
                    // this field, so keep it in sync with whatever's actually
                    // configured on the Settings page (organizationSettings.uri
                    // is fetched fresh above, on every login).
                    String configuredLogo = tuple.getT1().getUri();
                    if (configuredLogo != null && !configuredLogo.isBlank()) {
                        response.setOrganizationLogoUri(configuredLogo);
                    }
                    return response;
                })
                .onErrorReturn(response);
    }
}
