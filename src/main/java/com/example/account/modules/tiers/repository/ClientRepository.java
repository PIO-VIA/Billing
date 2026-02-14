package com.example.account.modules.tiers.repository;

import com.example.account.modules.tiers.model.entity.Client;
import com.example.account.modules.tiers.model.enums.TypeClient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for Client entity operations.
 */
@Repository
public interface ClientRepository extends R2dbcRepository<Client, UUID> {
    
    Mono<Client> findByUsername(String username);
    
    Mono<Client> findByEmail(String email);
    
    Mono<Client> findByCodeClient(String codeClient);
    
    Flux<Client> findByTypeClient(TypeClient typeClient);
    
    Mono<Boolean> existsByUsername(String username);
    
    Mono<Boolean> existsByEmail(String email);
    
    Mono<Boolean> existsByCodeClient(String codeClient);
    
    @Query("SELECT * FROM clients WHERE actif = true")
    Flux<Client> findAllActiveClients();
    
    @Query("SELECT COUNT(*) FROM clients WHERE actif = true")
    Mono<Long> countActiveClients();
}
