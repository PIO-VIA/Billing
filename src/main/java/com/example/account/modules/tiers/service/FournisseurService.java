package com.example.account.modules.tiers.service;

import com.example.account.modules.tiers.dto.FournisseurCreateRequest;
import com.example.account.modules.tiers.dto.FournisseurUpdateRequest;
import com.example.account.modules.tiers.dto.FournisseurResponse;
import com.example.account.modules.tiers.mapper.FournisseurMapper;
import com.example.account.modules.tiers.model.entity.Fournisseur;
import com.example.account.modules.tiers.repository.FournisseurRepository;
import com.example.account.modules.tiers.service.producer.FournisseurEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;
    private final FournisseurEventProducer fournisseurEventProducer;

    @Transactional
    public Mono<FournisseurResponse> createFournisseur(FournisseurCreateRequest request) {
        log.info("Création d'un nouveau fournisseur: {}", request.getUsername());

        return fournisseurRepository.existsByUsername(request.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Un fournisseur avec ce username existe déjà"));
                    }
                    return request.getEmail() != null ? fournisseurRepository.existsByEmail(request.getEmail()) : Mono.just(false);
                })
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new IllegalArgumentException("Un fournisseur avec cet email existe déjà"));
                    }
                    
                    Fournisseur fournisseur = fournisseurMapper.toEntity(request);
                    if (fournisseur.getIdFournisseur() == null) {
                        fournisseur.setIdFournisseur(UUID.randomUUID());
                    }
                    return fournisseurRepository.save(fournisseur);
                })
                .map(savedFournisseur -> {
                    FournisseurResponse response = fournisseurMapper.toResponse(savedFournisseur);
                    fournisseurEventProducer.publishFournisseurCreated(response);
                    log.info("Fournisseur créé avec succès: {}", savedFournisseur.getIdFournisseur());
                    return response;
                });
    }

    @Transactional
    public Mono<FournisseurResponse> updateFournisseur(UUID fournisseurId, FournisseurUpdateRequest request) {
        log.info("Mise à jour du fournisseur: {}", fournisseurId);

        return fournisseurRepository.findById(fournisseurId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId)))
                .flatMap(fournisseur -> {
                    fournisseurMapper.updateEntityFromRequest(request, fournisseur);
                    return fournisseurRepository.save(fournisseur);
                })
                .map(updatedFournisseur -> {
                    FournisseurResponse response = fournisseurMapper.toResponse(updatedFournisseur);
                    fournisseurEventProducer.publishFournisseurUpdated(response);
                    log.info("Fournisseur mis à jour avec succès: {}", fournisseurId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<FournisseurResponse> getFournisseurById(UUID fournisseurId) {
        log.info("Récupération du fournisseur: {}", fournisseurId);

        return fournisseurRepository.findById(fournisseurId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId)))
                .map(fournisseurMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<FournisseurResponse> getFournisseurByUsername(String username) {
        log.info("Récupération du fournisseur par username: {}", username);

        return fournisseurRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé avec username: " + username)))
                .map(fournisseurMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FournisseurResponse> getAllFournisseurs() {
        log.info("Récupération de tous les fournisseurs");
        return fournisseurRepository.findAll()
                .map(fournisseurMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FournisseurResponse> getActiveFournisseurs() {
        log.info("Récupération des fournisseurs actifs");
        return fournisseurRepository.findByActifTrue()
                .map(fournisseurMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FournisseurResponse> getFournisseursByCategorie(String categorie) {
        log.info("Récupération des fournisseurs par catégorie: {}", categorie);
        return fournisseurRepository.findByCategorie(categorie)
                .map(fournisseurMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteFournisseur(UUID fournisseurId) {
        log.info("Suppression du fournisseur: {}", fournisseurId);

        return fournisseurRepository.existsById(fournisseurId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId));
                    }
                    return fournisseurRepository.deleteById(fournisseurId)
                            .then(Mono.fromRunnable(() -> fournisseurEventProducer.publishFournisseurDeleted(fournisseurId)));
                })
                .then()
                .doOnSuccess(v -> log.info("Fournisseur supprimé avec succès: {}", fournisseurId));
    }

    @Transactional
    public Mono<FournisseurResponse> updateSolde(UUID fournisseurId, Double montant) {
        log.info("Mise à jour du solde du fournisseur {}: {}", fournisseurId, montant);

        return fournisseurRepository.findById(fournisseurId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId)))
                .flatMap(fournisseur -> {
                    fournisseur.setSoldeCourant(fournisseur.getSoldeCourant() + montant);
                    return fournisseurRepository.save(fournisseur);
                })
                .map(fournisseurMapper::toResponse);
    }

    @Transactional
    public Mono<FournisseurResponse> desactiverFournisseur(UUID fournisseurId) {
        log.info("Désactivation du fournisseur: {}", fournisseurId);

        return fournisseurRepository.findById(fournisseurId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId)))
                .flatMap(fournisseur -> {
                    fournisseur.setActif(false);
                    return fournisseurRepository.save(fournisseur);
                })
                .map(fournisseurMapper::toResponse);
    }

    @Transactional
    public Mono<FournisseurResponse> activerFournisseur(UUID fournisseurId) {
        log.info("Activation du fournisseur: {}", fournisseurId);

        return fournisseurRepository.findById(fournisseurId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé: " + fournisseurId)))
                .flatMap(fournisseur -> {
                    fournisseur.setActif(true);
                    return fournisseurRepository.save(fournisseur);
                })
                .map(fournisseurMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countActiveFournisseurs() {
        return fournisseurRepository.countByActifTrue();
    }
}
