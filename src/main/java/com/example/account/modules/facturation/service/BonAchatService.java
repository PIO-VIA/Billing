package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.BonAchatRequest;
import com.example.account.modules.facturation.dto.response.BonAchatResponse;
import com.example.account.modules.facturation.mapper.BonAchatMapper;
import com.example.account.modules.facturation.model.entity.BonAchat;
import com.example.account.modules.facturation.repository.BonAchatRepository;
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
public class BonAchatService {

    private final BonAchatRepository bonAchatRepository;
    private final BonAchatMapper bonAchatMapper;

    /**
     * POST - Créer un nouveau bon d'achat
     */
    @Transactional
    public Mono<BonAchatResponse> createBonAchat(BonAchatRequest request) {
        log.info("Création d'un nouveau bon d'achat, numéro: {}", request.getNumeroBonAchat());

        BonAchat bonAchat = bonAchatMapper.toEntity(request);
        return bonAchatRepository.save(bonAchat)
                .map(savedBonAchat -> {
                    log.debug("Bon d'achat sauvegardé avec succès: {}", savedBonAchat.getIdBonAchat());
                    return bonAchatMapper.toResponse(savedBonAchat);
                });
    }

    /**
     * PUT - Mettre à jour un bon d'achat existant
     */
    @Transactional
    public Mono<BonAchatResponse> updateBonAchat(UUID id, BonAchatRequest request) {
        log.info("Mise à jour du bon d'achat ID: {}", id);

        return bonAchatRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Bon d'achat non trouvé avec l'ID: " + id)))
                .flatMap(existingBonAchat -> {
                    bonAchatMapper.updateEntityFromRequest(request, existingBonAchat);
                    return bonAchatRepository.save(existingBonAchat);
                })
                .map(bonAchatMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<BonAchatResponse> getBonAchatById(UUID id) {
        return bonAchatRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon d'achat non trouvé: " + id)))
                .map(bonAchatMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<BonAchatResponse> getAllBonsAchat() {
        return bonAchatRepository.findAll()
                .map(bonAchatMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteBonAchat(UUID id) {
        return bonAchatRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Bon d'achat non trouvé: " + id));
                    }
                    return bonAchatRepository.deleteById(id);
                })
                .doOnSuccess(v -> log.info("Bon d'achat ID: {} supprimé", id));
    }
}