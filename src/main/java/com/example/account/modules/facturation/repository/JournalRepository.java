package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Journal;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface JournalRepository extends R2dbcRepository<Journal, UUID> {
    Mono<Journal> findByNomJournal(String nomJournal);
    Flux<Journal> findByType(String type);
    Mono<Boolean> existsByNomJournal(String nomJournal);
    Flux<Journal> findByNomJournalContaining(String query);
    
    @Query("SELECT COUNT(*) FROM journals WHERE type = :type")
    Mono<Long> countByType(String type);
}
