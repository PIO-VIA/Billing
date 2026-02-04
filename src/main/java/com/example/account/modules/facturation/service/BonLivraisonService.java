package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.BonLivraisonRequest;
import com.example.account.modules.facturation.dto.response.BonLivraisonResponse;
import com.example.account.modules.facturation.mapper.BonLivraisonMapper;
import com.example.account.modules.facturation.model.entity.BonLivraison;
import com.example.account.modules.facturation.model.entity.LigneBonLivraison;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import com.example.account.modules.facturation.repository.BonLivraisonRepository;
import com.example.account.modules.facturation.repository.LigneBonLivraisonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonLivraisonService {

    private final BonLivraisonRepository bonLivraisonRepository;
    private final LigneBonLivraisonRepository ligneBonLivraisonRepository;
    private final BonLivraisonMapper bonLivraisonMapper;

    @Transactional
    public Mono<BonLivraisonResponse> createBonLivraison(BonLivraisonRequest request) {
        log.info("Création d'un nouveau bon de livraison pour le client: {}", request.getIdClient());

        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    BonLivraison bonLivraison = bonLivraisonMapper.toEntity(request);
                    
                    if (bonLivraison.getNumeroBonLivraison() == null || bonLivraison.getNumeroBonLivraison().isBlank()) {
                        bonLivraison.setNumeroBonLivraison("BL-" + System.currentTimeMillis());
                    }

                    if (bonLivraison.getStatut() == null) {
                        bonLivraison.setStatut(StatutBonLivraison.EN_PREPARATION);
                    }

                    bonLivraison.setOrganizationId(orgId);
                    bonLivraison.setCreatedAt(LocalDateTime.now());
                    bonLivraison.setUpdatedAt(LocalDateTime.now());
                    bonLivraison.setLivraisonEffectuee(false);

                    java.util.List<LigneBonLivraison> tempLignes = bonLivraison.getLignes();
                    
                    return bonLivraisonRepository.save(bonLivraison)
                            .flatMap(savedBL -> {
                                if (tempLignes == null || tempLignes.isEmpty()) {
                                    return Mono.just(savedBL);
                                }
                                
                                return Flux.fromIterable(tempLignes)
                                        .map(ligne -> {
                                            ligne.setIdBonLivraison(savedBL.getIdBonLivraison());
                                            ligne.setOrganizationId(orgId);
                                            return ligne;
                                        })
                                        .flatMap(ligneBonLivraisonRepository::save)
                                        .collectList()
                                        .map(savedLignes -> {
                                            savedBL.setLignes(savedLignes);
                                            return savedBL;
                                        });
                            });
                })
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<BonLivraisonResponse> getBonLivraisonById(UUID id) {
        log.info("Récupération du bon de livraison: {}", id);
        return bonLivraisonRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id)))
                .flatMap(bonLivraison -> ligneBonLivraisonRepository.findByIdBonLivraison(id)
                        .collectList()
                        .map(lignes -> {
                            bonLivraison.setLignes(lignes);
                            return bonLivraison;
                        }))
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<BonLivraisonResponse> getAllBonLivraisons() {
        return bonLivraisonRepository.findAll()
                .flatMap(bl -> ligneBonLivraisonRepository.findByIdBonLivraison(bl.getIdBonLivraison())
                        .collectList()
                        .map(lignes -> {
                            bl.setLignes(lignes);
                            return bl;
                        }))
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<BonLivraisonResponse> getBonLivraisonsByClient(UUID idClient) {
        return bonLivraisonRepository.findByIdClient(idClient)
                .flatMap(bl -> ligneBonLivraisonRepository.findByIdBonLivraison(bl.getIdBonLivraison())
                        .collectList()
                        .map(lignes -> {
                            bl.setLignes(lignes);
                            return bl;
                        }))
                .map(bonLivraisonMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteBonLivraison(UUID id) {
        return bonLivraisonRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Bon de livraison non trouvé: " + id));
                    }
                    return ligneBonLivraisonRepository.findByIdBonLivraison(id)
                            .flatMap(ligneBonLivraisonRepository::delete)
                            .then(bonLivraisonRepository.deleteById(id));
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

                    return bonLivraisonRepository.save(bonLivraison)
                            .flatMap(savedBL -> ligneBonLivraisonRepository.findByIdBonLivraison(id)
                                    .collectList()
                                    .map(lignes -> {
                                        savedBL.setLignes(lignes);
                                        return savedBL;
                                    }));
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
                    return bonLivraisonRepository.save(bonLivraison)
                            .flatMap(savedBL -> ligneBonLivraisonRepository.findByIdBonLivraison(id)
                                    .collectList()
                                    .map(lignes -> {
                                        savedBL.setLignes(lignes);
                                        return savedBL;
                                    }));
                })
                .map(bonLivraisonMapper::toResponse);
    }
}
