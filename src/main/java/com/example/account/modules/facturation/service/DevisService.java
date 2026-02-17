package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.DevisCreateRequest;
import com.example.account.modules.facturation.dto.response.DevisResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.mapper.DevisMapper;
import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import com.example.account.modules.facturation.repository.DevisRepository;
import com.example.account.modules.facturation.service.ExternalServices.SellerService;
import com.example.account.modules.facturation.service.producer.DevisEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevisService {

    private final DevisRepository devisRepository;
    private final DevisMapper devisMapper;
    private final DevisEventProducer devisEventProducer;
    private final SellerService sellerService;
    private final org.springframework.data.r2dbc.core.R2dbcEntityTemplate entityTemplate;

    @Transactional
    public Mono<DevisResponse> createDevis(DevisCreateRequest request) {
        log.info("Création d'un nouveau devis pour le client: {}", request.getIdClient());

        Devis devis = devisMapper.toEntity(request);
        if (devis.getIdDevis() == null) {
            devis.setIdDevis(UUID.randomUUID());
        }
        devis.setCreatedAt(LocalDateTime.now());
        devis.setUpdatedAt(LocalDateTime.now());

        return entityTemplate.insert(devis)
                .map(savedDevis -> {
                    DevisResponse response = devisMapper.toResponse(savedDevis);
                    devisEventProducer.publishDevisCreated(response);
                    log.info("Devis créé avec succès: {}", savedDevis.getNumeroDevis());
                    return response;
                });
    }

    @Transactional
    public Mono<DevisResponse> updateDevis(UUID devisId, DevisCreateRequest request) {
        log.info("Mise à jour du devis: {}", devisId);

        return devisRepository.findById(devisId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId)))
                .flatMap(devis -> {
                    devisMapper.updateEntityFromRequest(request, devis);
                    devis.setUpdatedAt(LocalDateTime.now());
                    return devisRepository.save(devis);
                })
                .map(updatedDevis -> {
                    DevisResponse response = devisMapper.toResponse(updatedDevis);
                    devisEventProducer.publishDevisUpdated(response);
                    log.info("Devis mis à jour avec succès: {}", devisId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<DevisResponse> getDevisById(UUID devisId) {
        log.info("Récupération du devis: {}", devisId);

        return devisRepository.findById(devisId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId)))
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<DevisResponse> getDevisByNumero(String numeroDevis) {
        log.info("Récupération du devis par numéro: {}", numeroDevis);

        return devisRepository.findByNumeroDevis(numeroDevis)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé avec numéro: " + numeroDevis)))
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<DevisResponse> getAllDevis() {
        log.info("Récupération de tous les devis");
        return devisRepository.findAll()
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<DevisResponse> getAllDevis(Pageable pageable) {
        log.info("Récupération de tous les devis avec pagination");
        return devisRepository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisByClient(UUID clientId) {
        log.info("Récupération des devis du client: {}", clientId);
        return devisRepository.findByIdClient(clientId)
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisByStatut(StatutDevis statut) {
        log.info("Récupération des devis par statut: {}", statut);
        return devisRepository.findByStatut(statut)
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisExpires() {
        log.info("Récupération des devis expirés");
        return devisRepository.findExpiredDevis(LocalDate.now())
                .map(devisMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des devis entre {} et {}", dateDebut, dateFin);
        return devisRepository.findByDateCreationBetween(dateDebut, dateFin)
                .map(devisMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteDevis(UUID devisId) {
        log.info("Suppression du devis: {}", devisId);

        return devisRepository.existsById(devisId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId));
                    }
                    return devisRepository.deleteById(devisId)
                            .then(Mono.fromRunnable(() -> devisEventProducer.publishDevisDeleted(devisId)));
                })
                .then();
    }

    @Transactional
    public Mono<DevisResponse> accepterDevis(UUID devisId) {
        log.info("Acceptation du devis: {}", devisId);

        return devisRepository.findById(devisId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId)))
                .flatMap(devis -> {
                    devis.setStatut(StatutDevis.ACCEPTE);
                    devis.setDateAcceptation(LocalDateTime.now());
                    return devisRepository.save(devis);
                })
                .map(updatedDevis -> {
                    DevisResponse response = devisMapper.toResponse(updatedDevis);
                    devisEventProducer.publishDevisAccepted(response);
                    log.info("Devis accepté: {}", devisId);
                    return response;
                });
    }

    @Transactional
    public Mono<DevisResponse> refuserDevis(UUID devisId, String motifRefus) {
        log.info("Refus du devis: {}", devisId);

        return devisRepository.findById(devisId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId)))
                .flatMap(devis -> {
                    devis.setStatut(StatutDevis.REFUSE);
                    devis.setDateRefus(LocalDateTime.now());
                    devis.setMotifRefus(motifRefus);
                    return devisRepository.save(devis);
                })
                .map(devisMapper::toResponse);
    }

    public Flux<SellerAuthResponse> enrichDevis(UUID orgId) {
        return sellerService.getSellersByOrganization(orgId);
    }
}
