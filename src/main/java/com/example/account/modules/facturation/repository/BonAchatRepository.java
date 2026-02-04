package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonAchat;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BonAchatRepository extends R2dbcRepository<BonAchat, UUID> {

    // Recherche par numéro unique
    Mono<BonAchat> findByNumeroBonAchat(String numeroBonAchat);

    // Recherche par fournisseur
    Flux<BonAchat> findBySupplierId(UUID supplierId);

    // Vérification d'existence
    Mono<Boolean> existsByNumeroBonAchat(String numeroBonAchat);

    /**
     * Recherche tous les bons d'achat d'une organisation spécifique.
     */
    Flux<BonAchat> findByOrganizationId(UUID organizationId);

    /**
     * Recherche par numéro au sein d'une organisation précise.
     */
    Mono<BonAchat> findByNumeroBonAchatAndOrganizationId(String numeroBonAchat, UUID organizationId);
}