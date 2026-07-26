package com.example.account.modules.portal.controller;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.facturation.adapter.output.external.AccountingKernelAuthService;
import com.example.account.modules.facturation.domain.port.input.DevisUseCase;
import com.example.account.modules.facturation.domain.port.input.FactureUseCase;
import com.example.account.modules.facturation.dto.request.QuotationProposalCreateRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.dto.response.DevisResponse;
import com.example.account.modules.facturation.dto.response.FactureFournisseurResponse;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.facturation.dto.response.QuotationProposalResponse;
import com.example.account.modules.facturation.model.enums.StatutBonAchat;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.facturation.model.enums.StatutFactureFournisseur;
import com.example.account.modules.facturation.service.BonAchatService;
import com.example.account.modules.facturation.service.FactureFournisseurService;
import com.example.account.modules.facturation.service.QuotationProposalService;
import com.example.account.modules.portal.security.PortalIdentityResolver;
import com.example.account.modules.portal.security.PortalOrgAssociation;
import com.example.account.modules.portal.security.PortalPrincipal;
import com.example.account.modules.settings.service.SettingService;
import com.example.account.modules.tiers.domain.port.input.ClientUseCase;
import com.example.account.modules.tiers.dto.ClientResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only document access for the client-portal (see PortalAuthController).
 * The caller authenticates with a genuine Kernel access token — no separate
 * portal JWT — so identity is re-resolved per request via PortalIdentityResolver.
 * An actor can have a customer/supplier record in more than one organization
 * (a third-party record's id is per-org), so documents are aggregated across
 * every organization the resolved principal has a record in — never scoped
 * to a single client-supplied org. Each association's own clientId is used
 * to fetch that org's documents; never a client-supplied one.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PortalDataController {

    private final DevisUseCase devisService;
    private final FactureUseCase factureService;
    private final BonAchatService bonAchatService;
    private final FactureFournisseurService factureFournisseurService;
    private final QuotationProposalService quotationProposalService;
    private final ClientUseCase clientUseCase;
    private final SettingService settingService;
    private final PortalIdentityResolver identityResolver;
    private final AccountingKernelAuthService accountingKernelAuthService;

    @Qualifier("kernelWebClient")
    private final WebClient kernelWebClient;

    private Mono<PortalPrincipal> authenticate() {
        return ReactiveOrganizationContext.getBearerToken()
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing portal token")))
                .flatMap(this::resolvePrincipal);
    }

    private Mono<PortalPrincipal> resolvePrincipal(String token) {
        return identityResolver.resolveActor(token)
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired portal session")))
                .flatMap(actor -> identityResolver.resolveAssociations(actor.getActorId())
                        .flatMap(associations -> {
                            if (associations.isEmpty()) {
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "This account has no customer or supplier record in any organization."));
                            }
                            return Mono.just(PortalPrincipal.of(associations, actor.getEmail()));
                        }));
    }

    private void requireRole(PortalPrincipal principal, String role) {
        if (!principal.hasRole(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This document type isn't available for your account type");
        }
    }

    /**
     * "Sent to me" for the portal means anything past the internal draft
     * stage — once a document leaves BROUILLON it stays visible through
     * whatever it becomes next (ACCEPTE, PAYE, etc.), not just while it's
     * literally in the ENVOYE state.
     */
    @GetMapping("/api/portal/quotations")
    public Flux<DevisResponse> getMyQuotations() {
        return authenticate().flatMapMany(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("CUSTOMER");
            requireAnyAssociation(associations);
            return Flux.fromIterable(associations)
                    .flatMap(a -> devisService.getDevisByClientId(a.clientId())
                            .filter(d -> a.organizationId().equals(d.getOrganizationId()))
                            .filter(d -> d.getStatut() != StatutDevis.BROUILLON));
        });
    }

    @GetMapping("/api/portal/invoices")
    public Flux<FactureResponse> getMyInvoices() {
        return authenticate().flatMapMany(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("CUSTOMER");
            requireAnyAssociation(associations);
            return Flux.fromIterable(associations)
                    .flatMap(a -> factureService.getFacturesByClient(a.clientId())
                            .filter(f -> a.organizationId().equals(f.getOrganizationId()))
                            .filter(f -> f.getEtat() != StatutFacture.BROUILLON));
        });
    }

    @GetMapping("/api/portal/purchase-orders")
    public Flux<BonAchatResponse> getMyPurchaseOrders() {
        return authenticate().flatMapMany(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("SUPPLIER");
            requireAnyAssociation(associations);
            return Flux.fromIterable(associations)
                    .flatMap(a -> bonAchatService.getBySupplierId(a.clientId())
                            .filter(bo -> a.organizationId().equals(bo.getOrganizationId()))
                            .filter(bo -> bo.getStatus() != StatutBonAchat.BROUILLON));
        });
    }

    @GetMapping("/api/portal/supplier-invoices")
    public Flux<FactureFournisseurResponse> getMySupplierInvoices() {
        return authenticate().flatMapMany(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("SUPPLIER");
            requireAnyAssociation(associations);
            return Flux.fromIterable(associations)
                    .flatMap(a -> factureFournisseurService.getByOrganizationIdAndFournisseurId(a.organizationId(), a.clientId())
                            .filter(fi -> fi.getStatut() != StatutFactureFournisseur.BROUILLON));
        });
    }

    private void requireAnyAssociation(List<PortalOrgAssociation> associations) {
        if (associations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This document type isn't available for your account type");
        }
    }

    @PostMapping("/api/portal/quotations/{id}/accept")
    public Mono<ResponseEntity<Void>> acceptQuotation(@PathVariable UUID id) {
        return authenticate().flatMap(principal -> {
            requireRole(principal, "CUSTOMER");
            return devisService.getDevisById(id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Quotation not found")))
                    .flatMap(d -> {
                        requireOwnership(principal, d.getIdClient(), d.getOrganizationId());
                        return devisService.accepterDevis(id);
                    })
                    .thenReturn(ResponseEntity.ok().<Void>build());
        });
    }

    @PostMapping("/api/portal/quotations/{id}/reject")
    public Mono<ResponseEntity<Void>> rejectQuotation(@PathVariable UUID id) {
        return authenticate().flatMap(principal -> {
            requireRole(principal, "CUSTOMER");
            return devisService.getDevisById(id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Quotation not found")))
                    .flatMap(d -> {
                        requireOwnership(principal, d.getIdClient(), d.getOrganizationId());
                        return devisService.refuserDevis(id);
                    })
                    .thenReturn(ResponseEntity.ok().<Void>build());
        });
    }

    @PostMapping("/api/portal/purchase-orders/{id}/accept")
    public Mono<ResponseEntity<Void>> acceptPurchaseOrder(@PathVariable UUID id) {
        return authenticate().flatMap(principal -> {
            requireRole(principal, "SUPPLIER");
            return bonAchatService.getBonAchatById(id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase order not found")))
                    .flatMap(bo -> {
                        requireOwnership(principal, bo.getSupplierId() != null ? bo.getSupplierId().toString() : null, bo.getOrganizationId());
                        return bonAchatService.accepterBonAchat(id);
                    })
                    .thenReturn(ResponseEntity.ok().<Void>build());
        });
    }

    @PostMapping("/api/portal/purchase-orders/{id}/reject")
    public Mono<ResponseEntity<Void>> rejectPurchaseOrder(@PathVariable UUID id) {
        return authenticate().flatMap(principal -> {
            requireRole(principal, "SUPPLIER");
            return bonAchatService.getBonAchatById(id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase order not found")))
                    .flatMap(bo -> {
                        requireOwnership(principal, bo.getSupplierId() != null ? bo.getSupplierId().toString() : null, bo.getOrganizationId());
                        return bonAchatService.refuserBonAchat(id);
                    })
                    .thenReturn(ResponseEntity.ok().<Void>build());
        });
    }

    @PostMapping("/api/portal/quotation-proposals")
    public Mono<QuotationProposalResponse> proposeQuotation(@Valid @RequestBody QuotationProposalCreateRequest request) {
        return authenticate().flatMap(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("CUSTOMER");
            requireAnyAssociation(associations);
            // A client-supplied organizationId picks which of the account's orgs this
            // proposal is for; default to the first if none/unrecognized was supplied.
            PortalOrgAssociation target = associations.stream()
                    .filter(a -> a.organizationId().equals(request.getOrganizationId()))
                    .findFirst()
                    .orElse(associations.get(0));
            // Never trust the client-supplied idClient — always the resolved principal's.
            request.setIdClient(target.clientId());
            request.setOrganizationId(target.organizationId());
            request.setEmailClient(principal.email());

            if (request.getNomClient() != null) {
                return quotationProposalService.create(request);
            }
            return findClient(target)
                    .map(c -> c.getRaisonSociale() != null ? c.getRaisonSociale() : c.getUsername())
                    .defaultIfEmpty(target.clientDisplayName() != null ? target.clientDisplayName() : "")
                    .flatMap(name -> {
                        request.setNomClient(name);
                        return quotationProposalService.create(request);
                    });
        });
    }

    @GetMapping("/api/portal/quotation-proposals")
    public Flux<QuotationProposalResponse> getMyQuotationProposals() {
        return authenticate().flatMapMany(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("CUSTOMER");
            requireAnyAssociation(associations);
            return Flux.fromIterable(associations)
                    .flatMap(a -> quotationProposalService.getByClientAndOrganization(a.clientId(), a.organizationId()));
        });
    }

    @GetMapping("/api/portal/me/client")
    public Mono<ClientResponse> getMyClientInfo(@RequestParam(required = false) UUID organizationId) {
        return authenticate().flatMap(principal -> {
            List<PortalOrgAssociation> associations = principal.associationsWithRole("CUSTOMER");
            requireAnyAssociation(associations);
            // A person's client record differs per org (different code, credit
            // limit, allowed sale sizes, etc.) — use the one the caller asked for
            // if it's actually one of theirs, otherwise fall back to the first.
            PortalOrgAssociation target = associations.stream()
                    .filter(a -> a.organizationId().equals(organizationId))
                    .findFirst()
                    .orElse(associations.get(0));
            return findClient(target)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Client record not found")));
        });
    }

    /**
     * GET /api/customers (the underlying call behind getAllClients) requires
     * staff-level "third-parties:read" Kernel permission — a plain customer's
     * own login token gets 403 ACCESS_DENIED even looking up their own record.
     * So this deliberately does NOT forward the caller's token: it uses our
     * own service account instead, same as kernelWebClient does automatically
     * for calls with no Authorization header already set. Safe here because
     * the result is always filtered down to the caller's own resolved
     * clientId before anything is returned — never client-suppliable.
     */
    private Mono<ClientResponse> findClient(PortalOrgAssociation target) {
        return accountingKernelAuthService.getValidToken()
                .flatMap(token -> clientUseCase.getAllClients()
                        .filter(c -> target.clientId().equals(c.getIdClient()))
                        .next()
                        .contextWrite(ctx -> ctx
                                .put(ReactiveOrganizationContext.ORGANIZATION_ID_KEY, target.organizationId())
                                .put(ReactiveOrganizationContext.TOKEN_KEY, token)));
    }

    @GetMapping("/api/portal/organization")
    public Mono<Map> getMyOrganizationBranding() {
        return authenticate().flatMap(principal -> {
            // Branding is inherently single-org — show the first association's.
            UUID organizationId = principal.associations().get(0).organizationId();
            return Mono.zip(
                    kernelWebClient.get()
                            .uri("/api/organizations/{id}/branding", organizationId)
                            .retrieve()
                            .bodyToMono(Map.class),
                    settingService.getOrganizationSettings(organizationId)
            ).map(tuple -> {
                // The Kernel branding snapshot's logo can be stale — override with
                // whatever's actually configured on the Settings page, same as the
                // seller-login flow does.
                Map<String, Object> branding = new java.util.HashMap<>(tuple.getT1());
                String configuredLogo = tuple.getT2().getUri();
                if (configuredLogo != null && !configuredLogo.isBlank()) {
                    branding.put("organizationLogoUri", configuredLogo);
                }
                return branding;
            });
        });
    }

    private void requireOwnership(PortalPrincipal principal, String documentPartyId, UUID documentOrgId) {
        boolean owns = documentPartyId != null && principal.associations().stream()
                .anyMatch(a -> a.organizationId().equals(documentOrgId) && a.clientId().toString().equalsIgnoreCase(documentPartyId));
        if (!owns) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This document doesn't belong to your account");
        }
    }
}
