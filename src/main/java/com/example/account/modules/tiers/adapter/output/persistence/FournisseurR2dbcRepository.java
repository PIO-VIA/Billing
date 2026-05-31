package com.example.account.modules.tiers.adapter.output.persistence;

import com.example.account.modules.tiers.domain.model.enums.TypeClient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface FournisseurR2dbcRepository extends R2dbcRepository<FournisseurPersistenceEntity, UUID> {
    Mono<FournisseurPersistenceEntity> findByUsername(String username);
    Mono<FournisseurPersistenceEntity> findByEmail(String email);
    Mono<FournisseurPersistenceEntity> findByCodeFournisseur(String codeFournisseur);
    Flux<FournisseurPersistenceEntity> findByTypeFournisseur(TypeClient typeFournisseur);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    
    @Query("SELECT * FROM fournisseurs WHERE actif = true")
    Flux<FournisseurPersistenceEntity> findAllActiveFournisseurs();
    
    @Query("SELECT count(*) FROM fournisseurs WHERE actif = true")
    Mono<Long> countActiveFournisseurs();
}
