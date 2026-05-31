package com.example.account.modules.facturation.application.usecase.impl;

import com.example.account.modules.facturation.domain.model.Devis;
import com.example.account.modules.facturation.domain.port.input.DevisUseCase;
import com.example.account.modules.facturation.domain.port.output.DevisEventPort;
import com.example.account.modules.facturation.domain.port.output.DevisRepositoryPort;
import com.example.account.modules.facturation.domain.port.output.SellerServicePort;
import com.example.account.modules.facturation.dto.request.DevisCreateRequest;
import com.example.account.modules.facturation.dto.response.DevisResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.mapper.DevisMapper;
import com.example.account.modules.facturation.model.enums.StatutDevis;
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
public class DevisUseCaseImpl implements DevisUseCase {

    private final DevisRepositoryPort devisRepository;
    private final DevisMapper devisMapper; // Assuming we use the existing DevisMapper or a modified one. Wait, the old DevisMapper uses entity. Let's see. 
    // Actually, DevisMapper maps from DTO to Domain now because Devis is the domain class!
    // The old DevisMapper mapped DTO to entity. Now "entity" IS the domain.
    private final DevisEventPort devisEventProducer;
    private final SellerServicePort sellerService;

    @Override
    @Transactional
    public Mono<DevisResponse> createDevis(DevisCreateRequest request) {
        log.info("Création d'un nouveau devis pour le client: {}", request.getIdClient());

        // Wait, DevisMapper maps to the entity class in com.example.account.modules.facturation.model.entity.Devis
        // I need to use the Domain class instead.
        // For now, let's assume DevisMapper maps to Domain. 
        // We will need to check DevisMapper later.
        Devis devis = devisMapper.toDomain(request);
        if (devis.getIdDevis() == null) {
            devis.setIdDevis(UUID.randomUUID());
        }
        
        devis.setUpdatedAt(LocalDateTime.now());

        return devisRepository.insert(devis)
                .map(savedDevis -> {
                    DevisResponse response = devisMapper.toResponse(savedDevis);
                    devisEventProducer.publishDevisCreated(response);
                    log.info("Devis créé avec succès: {}", savedDevis.getNumeroDevis());
                    return response;
                });
    }

    @Override
    @Transactional
    public Mono<DevisResponse> updateDevis(UUID devisId, DevisCreateRequest request) {
        log.info("Mise à jour du devis: {}", devisId);

        return devisRepository.findById(devisId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId)))
                .flatMap(devis -> {
                    devisMapper.updateDomainFromRequest(request, devis);
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

    @Override
    @Transactional(readOnly = true)
    public Mono<DevisResponse> getDevisById(UUID devisId) {
        log.info("Récupération du devis: {}", devisId);

        return devisRepository.findById(devisId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé: " + devisId)))
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DevisResponse> getDevisByNumero(String numeroDevis) {
        log.info("Récupération du devis par numéro: {}", numeroDevis);

        return devisRepository.findByNumeroDevis(numeroDevis)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Devis non trouvé avec numéro: " + numeroDevis)))
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisResponse> getAllDevis() {
        log.info("Récupération de tous les devis");
        return devisRepository.findAll()
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisResponse> getAllDevis(Pageable pageable) {
        log.info("Récupération de tous les devis avec pagination");
        return devisRepository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisByClient(UUID clientId) {
        log.info("Récupération des devis du client: {}", clientId);
        return devisRepository.findByIdClient(clientId)
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisByStatut(StatutDevis statut) {
        log.info("Récupération des devis par statut: {}", statut);
        return devisRepository.findByStatut(statut)
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisExpires() {
        log.info("Récupération des devis expirés");
        return devisRepository.findExpiredDevis(LocalDate.now())
                .map(devisMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisResponse> getDevisByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des devis entre {} et {}", dateDebut, dateFin);
        return devisRepository.findByDateCreationBetween(dateDebut, dateFin)
                .map(devisMapper::toResponse);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Flux<SellerAuthResponse> enrichDevis(UUID orgId) {
        return sellerService.getSellersByOrganization(orgId);
    }
}
