package com.example.account.modules.facturation.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.mapper.BondeReceptionMapper;
import com.example.account.modules.facturation.model.entity.BondeReception;
import com.example.account.modules.facturation.repository.BonReceptionRepository;
import com.example.account.modules.facturation.dto.request.AssignDocPermissionRequest;
import com.example.account.modules.facturation.model.enums.DocPermissionLevel;
import com.example.account.modules.facturation.model.enums.DocType;
import com.example.account.modules.tiers.domain.port.input.FournisseurUseCase;
import com.example.account.modules.tiers.dto.FournisseurResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BonReceptionService {

    private final BonReceptionRepository bonReceptionRepository;
    private final BondeReceptionMapper bondeReceptionMapper;
    private final R2dbcEntityTemplate entityTemplate;
    private final DocPermissionService docPermissionService;
    private final FournisseurUseCase fournisseurUseCase;
    private final EmailService emailService;

    @Value("${client-portal.frontend-url}")
    private String portalFrontendUrl;

    private <T> Mono<T> grantOwnerPermission(UUID sellerId, UUID docId, T response) {
        if (sellerId == null || docId == null) return Mono.just(response);
        AssignDocPermissionRequest request = new AssignDocPermissionRequest();
        request.setSellerId(sellerId);
        request.setDocId(docId);
        request.setDocType(DocType.BON_RECEPTION);
        request.setPermission(DocPermissionLevel.OWNER);
        return docPermissionService.grant(request)
                .thenReturn(response)
                .onErrorResume(e -> {
                    log.error("Failed to grant owner doc-permission for bon reception {}: {}", docId, e.getMessage());
                    return Mono.just(response);
                });
    }

    @Transactional
    public Mono<BondeReceptionResponse> createBondeReception(BondeReceptionCreateRequest dto) {
        log.info("Création d'un nouveau Bon de Réception");
        BondeReception bondeReception = bondeReceptionMapper.toEntity(dto);
        if (bondeReception.getIdBonReception() == null) {
            bondeReception.setIdBonReception(UUID.randomUUID());
        }
        return entityTemplate.insert(bondeReception)
                .flatMap(saved -> grantOwnerPermission(saved.getCreatedBy(), saved.getIdBonReception(), bondeReceptionMapper.toDto(saved)));
    }

    @Transactional(readOnly = true)
    public Flux<BondeReceptionResponse> getAllBondeReception() {
        log.info("Récupération de tous les Bons de Réception");
        return bonReceptionRepository.findAll()
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<BondeReceptionResponse> getBondeReceptionById(UUID id) {
        log.info("Récupération du Bon de Réception ID: {}", id);
        // Search with reactive context support via ReactiveOrganizationContext
        return bonReceptionRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé")))
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional
    public Mono<BondeReceptionResponse> updateBondeReception(UUID id, BondeReceptionResponse dto) {
        log.info("Mise à jour du Bon de Réception: {}", id);
        return bonReceptionRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé")))
                .flatMap(bondeReception -> {
                    UUID originalId = bondeReception.getIdBonReception();
                    UUID originalOrgId = bondeReception.getOrganizationId();
                    bondeReceptionMapper.updateEntityFromDto(dto, bondeReception);
                    bondeReception.setIdBonReception(originalId);
                    if (bondeReception.getOrganizationId() == null) {
                        bondeReception.setOrganizationId(originalOrgId);
                    }
                    return bonReceptionRepository.save(bondeReception);
                })
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional
    public Mono<Void> deleteBondeReception(UUID id) {
        log.info("Suppression du Bon de Réception: {}", id);
        return bonReceptionRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé")))
                .flatMap(bondeReception -> bonReceptionRepository.delete(bondeReception));
    }

    @Transactional(readOnly = true)
    public Flux<BondeReceptionResponse> getByOrganizationId(UUID organizationId) {
        return bonReceptionRepository.findByOrganizationId(organizationId).map(bondeReceptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<BondeReceptionResponse> getByAgencyId(UUID agencyId) {
        return bonReceptionRepository.findByAgencyId(agencyId).map(bondeReceptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<BondeReceptionResponse> getBySellerId(UUID sellerId) {
        return docPermissionService.findBySellerAndDocType(sellerId, DocType.BON_RECEPTION)
                .flatMap(permission -> bonReceptionRepository.findById(permission.getDocId())
                        .map(entity -> {
                            BondeReceptionResponse response = bondeReceptionMapper.toDto(entity);
                            response.setDocPermission(docPermissionService.toResponse(permission));
                            return response;
                        }));
    }

    /**
     * Notifies the fournisseur that this goods-receipt note was recorded —
     * same pattern as DevisUseCaseImpl.sendToPortal / BonAchatService.sendToPortal,
     * bootstrapping their (login-based) portal access only if they've never
     * had it, then emailing a "new document" notification. Deliberately
     * doesn't touch `statut` (that tracks how much was received, e.g.
     * RECEIVED/PARTIALLY_RECEIVED — orthogonal to whether the fournisseur was
     * notified), so this uses its own envoyeParEmail/dateEnvoiEmail flag
     * instead of a status transition. Idempotent: a bon already marked
     * envoyeParEmail is a no-op, so double-submits don't re-provision access
     * or re-send the email. envoyeParEmail is only persisted once the email
     * has actually gone out, so a failed send can be retried.
     */
    @Transactional
    public Mono<Void> sendToPortal(UUID id) {
        log.info("Envoi du bon de réception {} vers le fournisseur", id);
        return bonReceptionRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé: " + id)))
                .flatMap(bonReception -> {
                    if (Boolean.TRUE.equals(bonReception.getEnvoyeParEmail())) {
                        log.info("Bon de réception {} déjà envoyé, envoi ignoré", id);
                        return Mono.empty();
                    }
                    if (bonReception.getIdFournisseur() == null) {
                        return Mono.error(new IllegalStateException("Le bon de réception n'a pas de fournisseur renseigné."));
                    }
                    return fournisseurUseCase.getFournisseurById(bonReception.getIdFournisseur())
                            .switchIfEmpty(Mono.error(new IllegalStateException("Fournisseur introuvable: " + bonReception.getIdFournisseur())))
                            .flatMap(fournisseur -> {
                                String email = fournisseur.getEmail();
                                if (email == null || email.isBlank()) {
                                    return Mono.error(new IllegalStateException("Le fournisseur n'a pas d'adresse email renseignée."));
                                }
                                String recipientName = bonReception.getNomFournisseur() != null
                                        ? bonReception.getNomFournisseur() : fournisseur.getRaisonSociale();
                                return fournisseurUseCase.ensureFournisseurPortalAccess(
                                                bonReception.getIdFournisseur(), email, recipientName)
                                        // Kernel's ensure-portal-access endpoint isn't deployed yet (404) —
                                        // this is a bootstrap-only nicety anyway (grants login to brand-new
                                        // fournisseurs), so a failure here shouldn't block the actual email.
                                        .onErrorResume(e -> {
                                            log.warn("ensureFournisseurPortalAccess failed for bon de réception {} (continuing anyway): {}", id, e.getMessage());
                                            return Mono.empty();
                                        })
                                        .then(emailService.sendPortalDocumentNotification(
                                                email, recipientName,
                                                "Bon de réception", bonReception.getNumeroReception(),
                                                portalFrontendUrl + "/portal/login"));
                            })
                            .then(Mono.defer(() -> {
                                bonReception.setEnvoyeParEmail(true);
                                bonReception.setDateEnvoiEmail(LocalDateTime.now());
                                return bonReceptionRepository.save(bonReception);
                            }));
                })
                .then();
    }
}
