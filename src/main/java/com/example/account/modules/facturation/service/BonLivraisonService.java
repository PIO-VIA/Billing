package com.example.account.modules.facturation.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.account.modules.facturation.dto.request.BonLivraisonRequest;
import com.example.account.modules.facturation.dto.response.BonLivraisonResponse;
import com.example.account.modules.facturation.mapper.BonLivraisonMapper;
import com.example.account.modules.facturation.model.entity.BonLivraison;
import com.example.account.modules.facturation.model.entity.LigneBonLivraison;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import com.example.account.modules.facturation.repository.BonLivraisonRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonLivraisonService {

    private final BonLivraisonRepository bonLivraisonRepository;
    private final BonLivraisonMapper bonLivraisonMapper;
    private final R2dbcEntityTemplate entityTemplate;

    @Transactional
    public Mono<BonLivraisonResponse> createBonLivraison(BonLivraisonRequest request) {
        log.info("Création d'un nouveau bon de livraison pour le client: {}", request.getIdClient());

        BonLivraison bonLivraison = bonLivraisonMapper.toEntity(request);
        if (bonLivraison.getIdBonLivraison() == null) {
            bonLivraison.setIdBonLivraison(UUID.randomUUID());
        }

        return entityTemplate.insert(bonLivraison)
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<BonLivraisonResponse> getBonLivraisonById(UUID id) {
        log.info("Récupération du bon de livraison: {}", id);
        return bonLivraisonRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id)))
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<BonLivraisonResponse> getAllBonLivraisons() {
        log.info("Récupération de tous les bons de livraison");
        return bonLivraisonRepository.findAll()
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<BonLivraisonResponse> getBonLivraisonsByClient(UUID idClient) {
        log.info("Récupération des bons de livraison du client: {}", idClient);
        return bonLivraisonRepository.findByIdClient(idClient)
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteBonLivraison(UUID id) {
        log.info("Suppression du bon de livraison: {}", id);
        return bonLivraisonRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id));
                    }
                    return bonLivraisonRepository.deleteById(id);
                });
    }

    @Transactional
    public Mono<BonLivraisonResponse> marquerCommeEffectuee(UUID id) {
        log.info("Marquage du bon de livraison {} comme effectuée", id);
        return bonLivraisonRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id)))
                .flatMap(bonLivraison -> {
                    if (Boolean.TRUE.equals(bonLivraison.getLivraisonEffectuee())) {
                        return Mono.error(new IllegalStateException("La livraison a déjà été effectuée"));
                    }

                    bonLivraison.setLivraisonEffectuee(true);
                    bonLivraison.setDateLivraisonEffective(LocalDateTime.now());
                    bonLivraison.setStatut(StatutBonLivraison.LIVRE);
                    bonLivraison.setUpdatedAt(LocalDateTime.now());

                    return bonLivraisonRepository.save(bonLivraison);
                })
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional
    public Mono<BonLivraisonResponse> updateStatut(UUID id, StatutBonLivraison nouveauStatut) {
        log.info("Mise à jour du statut du bon de livraison {} vers {}", id, nouveauStatut);
        return bonLivraisonRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id)))
                .flatMap(bonLivraison -> {
                    bonLivraison.setStatut(nouveauStatut);
                    bonLivraison.setUpdatedAt(LocalDateTime.now());
                    return bonLivraisonRepository.save(bonLivraison);
                })
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional
    public Mono<BonLivraisonResponse> update(UUID id, BonLivraisonRequest request) {
        log.info("Mise à jour du bon de livraison {}", id);
        return bonLivraisonRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id)))
                .flatMap(bonLivraison -> {
                    bonLivraisonMapper.updateEntityFromDTO(request, bonLivraison);
                    bonLivraison.setUpdatedAt(LocalDateTime.now());
                    return bonLivraisonRepository.save(bonLivraison);
                })
                .map(bonLivraisonMapper::toResponse);
    }
}
