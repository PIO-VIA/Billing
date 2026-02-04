package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Journal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface JournalRepository extends R2dbcRepository<Journal, UUID> {

    Flux<Journal> findAllBy(Pageable pageable);

    Mono<Journal> findByNomJournal(String nomJournal);

    Flux<Journal> findByType(String type);

    @Query("SELECT * FROM journals j WHERE j.nom_journal LIKE :nomJournal")
    Flux<Journal> findByNomJournalContaining(@Param("nomJournal") String nomJournal);

    @Query("SELECT COUNT(*) FROM journals j WHERE j.type = :type")
    Mono<Long> countByType(@Param("type") String type);

    Mono<Boolean> existsByNomJournal(String nomJournal);
}
