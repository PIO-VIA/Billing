package com.example.account.modules.tiers.adapter.output.persistence;

import com.example.account.modules.tiers.domain.model.enums.TypeClient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ClientR2dbcRepository extends R2dbcRepository<ClientPersistenceEntity, UUID> {
    Mono<ClientPersistenceEntity> findByUsername(String username);
    Mono<ClientPersistenceEntity> findByEmail(String email);
    Mono<ClientPersistenceEntity> findByCodeClient(String codeClient);
    Flux<ClientPersistenceEntity> findByTypeClient(TypeClient typeClient);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    
    @Query("SELECT * FROM clients WHERE actif = true")
    Flux<ClientPersistenceEntity> findAllActiveClients();
    
    @Query("SELECT count(*) FROM clients WHERE actif = true")
    Mono<Long> countActiveClients();
}
