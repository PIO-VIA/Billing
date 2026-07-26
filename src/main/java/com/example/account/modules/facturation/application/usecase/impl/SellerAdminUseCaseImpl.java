package com.example.account.modules.facturation.application.usecase.impl;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.domain.port.output.AgencyServicePort;
import com.example.account.modules.facturation.domain.port.input.SellerAdminUseCase;
import com.example.account.modules.facturation.domain.port.output.SellerServicePort;
import com.example.account.modules.facturation.dto.request.AssignAgencyRequest;
import com.example.account.modules.facturation.dto.request.CreateSellerRequest;
import com.example.account.modules.facturation.dto.request.SellerUIPermissionsRequest;
import com.example.account.modules.facturation.dto.request.UpdateSellerPermissionsRequest;
import com.example.account.modules.facturation.dto.request.UpdateSellerPhotoRequest;
import com.example.account.modules.facturation.dto.response.ExternalResponses.AssignAgencyResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.CreateSellerResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerListItemResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerUIPermissionsResponse;
import com.example.account.modules.facturation.model.entity.SellerAgencyAssignment;
import com.example.account.modules.facturation.model.entity.SellerAuthContext;
import com.example.account.modules.facturation.repository.SellerAgencyAssignmentRepository;
import com.example.account.modules.facturation.repository.SellerAuthContextRepository;
import com.example.account.modules.notification.dto.SendSellerInvitationEmailRequest;
import com.example.account.modules.notification.service.SellerEmailService;
import com.example.account.modules.shared.dto.kernel.KernelAgencyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerAdminUseCaseImpl implements SellerAdminUseCase {

    private static final SecureRandom PIN_RANDOM = new SecureRandom();

    private final SellerServicePort sellerServicePort;
    private final R2dbcEntityTemplate entityTemplate;
    private final SellerEmailService sellerEmailService;
    private final AgencyServicePort agencyServicePort;
    private final SellerAgencyAssignmentRepository sellerAgencyAssignmentRepository;
    private final SellerAuthContextRepository sellerAuthContextRepository;

    /**
     * sales-core has no concept of the POS quick-login PIN at all — it's
     * generated and stored purely in Billing's own seller_auth_contexts table
     * at seller-creation time (see saveAuthContext below) and handed back
     * once, in the create response. Listing sellers was a straight forward
     * to sales-core's own record, which never had the PIN to begin with, so
     * the column always showed blank — merged in here the same way agency is.
     */
    @Override
    public Flux<SellerListItemResponse> listSellers(UUID organizationId) {
        return sellerServicePort.listSellers(organizationId)
                .collectList()
                .flatMapMany(sellers -> {
                    List<UUID> sellerIds = sellers.stream().map(SellerListItemResponse::getId).toList();
                    return Mono.zip(
                                    sellerAgencyAssignmentRepository.findBySellerIdIn(sellerIds)
                                            .collectMap(SellerAgencyAssignment::getSellerId, Function.identity()),
                                    sellerAuthContextRepository.findBySellerIdIn(sellerIds)
                                            .collectMap(SellerAuthContext::getSellerId, Function.identity()))
                            .flatMapMany(tuple -> Flux.fromIterable(sellers)
                                    .doOnNext(seller -> {
                                        applyAssignment(seller, tuple.getT1().get(seller.getId()));
                                        applyAuthContext(seller, tuple.getT2().get(seller.getId()));
                                    }));
                });
    }

    private void applyAssignment(SellerListItemResponse seller, SellerAgencyAssignment assignment) {
        if (assignment == null) {
            return;
        }
        seller.setAgencyId(assignment.getAgencyId());
        seller.setAgency(assignment.getAgencyName());
    }

    private void applyAuthContext(SellerListItemResponse seller, SellerAuthContext authContext) {
        if (authContext == null) {
            return;
        }
        seller.setPin(authContext.getPin());
    }

    @Override
    public Mono<CreateSellerResponse> createSeller(CreateSellerRequest request) {
        return sellerServicePort.createSeller(request)
                .flatMap(response -> {
                    String pin = generatePin();
                    response.setPin(pin);
                    return saveAuthContext(response.getId(), request.getOrganizationId(), pin)
                            .then(sendInvitation(request, response, pin))
                            .thenReturn(response);
                });
    }

    private Mono<SellerAuthContext> saveAuthContext(UUID sellerId, UUID organizationId, String pin) {
        LocalDateTime now = LocalDateTime.now();
        SellerAuthContext context = SellerAuthContext.builder()
                .id(UUID.randomUUID())
                .sellerId(sellerId)
                .organizationId(organizationId)
                .pin(pin)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return entityTemplate.insert(context);
    }

    private Mono<Void> sendInvitation(CreateSellerRequest request, CreateSellerResponse response, String pin) {
        SendSellerInvitationEmailRequest invitation = new SendSellerInvitationEmailRequest();
        invitation.setOrganizationId(request.getOrganizationId());
        invitation.setEmail(request.getEmail());
        invitation.setUsername(response.getUsername());
        invitation.setRole(response.getRole());
        invitation.setAgency(request.getAgency());
        invitation.setPin(pin);
        return sellerEmailService.sendInvitation(invitation)
                .onErrorResume(e -> {
                    log.error("Failed to send seller invitation email to {}: {}", request.getEmail(), e.getMessage());
                    return Mono.empty();
                });
    }

    private String generatePin() {
        return String.format("%05d", PIN_RANDOM.nextInt(100_000));
    }

    /**
     * Handled entirely in Billing rather than forwarded to sales-core's own
     * /api/sellers/{id}/agency: that endpoint needs an internal Kernel
     * round-trip (sales-core's own service-account login) that 401s whenever
     * KERNEL_BASE_URL isn't set in that deployment's environment — a config
     * issue on Kernel's side, not fixable from here. Billing already has its
     * own working Kernel connection (AgencyServicePort) to validate the
     * agency, so the assignment is validated and stored locally instead.
     *
     * Deliberately doesn't call sellerServicePort.getById() to resolve the
     * seller's org — that GET /api/sellers/{id} is also broken on the live
     * sales-core deployment (405, unrelated to this fix). organizationId
     * comes from the request context instead, the same X-Organization-ID
     * header every other seller-admin call already relies on.
     */
    @Override
    public Mono<AssignAgencyResponse> assignAgency(UUID sellerId, AssignAgencyRequest request) {
        return ReactiveOrganizationContext.getOrganizationId()
                .flatMap(organizationId -> agencyServicePort.findById(organizationId, request.getAgencyId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Agency not found in this organization: " + request.getAgencyId())))
                        .flatMap(agency -> upsertAssignment(sellerId, organizationId, agency)));
    }

    /**
     * Spring Data R2DBC's save() decides insert-vs-update purely by whether the
     * @Id field is already set — this entity doesn't implement Persistable, so
     * a freshly-built row with a client-generated id would be misread as an
     * "existing" row to update and fail with "does not exist". Same reasoning
     * as saveAuthContext() above: use entityTemplate.insert() explicitly for
     * the genuinely-new case, save() only for a row that's confirmed to exist.
     */
    private Mono<AssignAgencyResponse> upsertAssignment(UUID sellerId, UUID organizationId, KernelAgencyResponse agency) {
        LocalDateTime now = LocalDateTime.now();
        return sellerAgencyAssignmentRepository.findBySellerId(sellerId)
                .flatMap(existing -> {
                    applyAgencyFields(existing, organizationId, agency, now);
                    return sellerAgencyAssignmentRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    SellerAgencyAssignment created = SellerAgencyAssignment.builder()
                            .id(UUID.randomUUID())
                            .sellerId(sellerId)
                            .createdAt(now)
                            .build();
                    applyAgencyFields(created, organizationId, agency, now);
                    return entityTemplate.insert(created);
                }))
                .map(assignment -> toAssignAgencyResponse(sellerId, assignment));
    }

    private void applyAgencyFields(SellerAgencyAssignment assignment, UUID organizationId, KernelAgencyResponse agency, LocalDateTime now) {
        assignment.setOrganizationId(organizationId);
        assignment.setAgencyId(agency.getId());
        assignment.setAgencyName(agency.getName());
        assignment.setAgencyEmail(agency.getEmail());
        assignment.setAgencyPhone(agency.getPhone());
        assignment.setAgencyCity(agency.getCity());
        assignment.setAgencyAddress(agency.getLocation());
        assignment.setUpdatedAt(now);
    }

    private AssignAgencyResponse toAssignAgencyResponse(UUID sellerId, SellerAgencyAssignment assignment) {
        AssignAgencyResponse response = new AssignAgencyResponse();
        response.setSellerId(sellerId);
        response.setAgencyId(assignment.getAgencyId());
        response.setAgency(assignment.getAgencyName());
        response.setAgencyEmail(assignment.getAgencyEmail());
        response.setAgencyPhone(assignment.getAgencyPhone());
        response.setAgencyCity(assignment.getAgencyCity());
        response.setAgencyAddress(assignment.getAgencyAddress());
        return response;
    }

    @Override
    public Mono<SellerUIPermissionsResponse> getUIPermissions(UUID sellerId) {
        return sellerServicePort.getUIPermissions(sellerId);
    }

    @Override
    public Mono<SellerUIPermissionsResponse> setUIPermissions(UUID sellerId, SellerUIPermissionsRequest request) {
        return sellerServicePort.setUIPermissions(sellerId, request);
    }

    @Override
    public Mono<SellerListItemResponse> updatePermissions(UUID sellerId, UpdateSellerPermissionsRequest request) {
        return sellerServicePort.updatePermissions(sellerId, request);
    }

    @Override
    public Mono<SellerListItemResponse> updatePhoto(UUID sellerId, UpdateSellerPhotoRequest request) {
        return sellerServicePort.updatePhoto(sellerId, request);
    }

    @Override
    public Mono<Void> deleteSeller(UUID sellerId) {
        return sellerServicePort.deleteSeller(sellerId);
    }
}
