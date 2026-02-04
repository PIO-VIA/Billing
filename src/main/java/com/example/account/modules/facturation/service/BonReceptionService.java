package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.mapper.BondeReceptionMapper;
import com.example.account.modules.facturation.model.entity.BondeReception;
import com.example.account.modules.facturation.repository.BonReceptionRepository;
import com.example.account.modules.core.context.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BonReceptionService {

    private final BonReceptionRepository bonReceptionRepository;
    private final BondeReceptionMapper bondeReceptionMapper;

    @Transactional
    public Mono<BondeReceptionResponse> createBondeReception(BondeReceptionCreateRequest dto) {
        log.info("Création d'un nouveau Bon de Réception");
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    BondeReception bondeReception = bondeReceptionMapper.toEntity(dto);
                    bondeReception.setOrganizationId(orgId);
                    bondeReception.setCreatedAt(LocalDateTime.now());
                    bondeReception.setUpdatedAt(LocalDateTime.now());
                    return bonReceptionRepository.save(bondeReception);
                })
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<BondeReceptionResponse> getAllBondeReception() {
        return OrganizationContext.getOrganizationId()
                .flatMapMany(bonReceptionRepository::findByOrganizationId)
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Mono<BondeReceptionResponse> getBondeReceptionById(UUID id) {
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> bonReceptionRepository.findByIdGRNAndOrganizationId(id, orgId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé")))
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional
    public Mono<BondeReceptionResponse> updateBondeReception(UUID id, BondeReceptionResponse dto) {
        log.info("Mise à jour du Bon de Réception: {}", id);
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> bonReceptionRepository.findByIdGRNAndOrganizationId(id, orgId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé")))
                        .flatMap(bondeReception -> {
                            bondeReceptionMapper.updateEntityFromDto(dto, bondeReception);
                            bondeReception.setUpdatedAt(LocalDateTime.now());
                            return bonReceptionRepository.save(bondeReception);
                        }))
                .map(bondeReceptionMapper::toDto);
    }

    @Transactional
    public Mono<Void> deleteBondeReception(UUID id) {
        log.info("Suppression du Bon de Réception: {}", id);
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> bonReceptionRepository.findByIdGRNAndOrganizationId(id, orgId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Bon de Réception non trouvé")))
                        .flatMap(bonReceptionRepository::delete));
    }
}
