package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.NoteCredit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface NoteCreditRepository extends R2dbcRepository<NoteCredit, UUID> {
    Mono<NoteCredit> findByNumeroNoteCredit(String numeroNoteCredit);
    Flux<NoteCredit> findByIdClient(UUID idClient);
    Flux<NoteCredit> findByIdFactureOrigine(UUID idFactureOrigine);
    Mono<Boolean> existsByNumeroNoteCredit(String numeroNoteCredit);
}
