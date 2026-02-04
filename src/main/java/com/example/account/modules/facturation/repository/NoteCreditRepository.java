package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.NoteCredit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface NoteCreditRepository extends R2dbcRepository<NoteCredit, UUID> {
    Flux<NoteCredit> findByOrganizationId(UUID organizationId);
    Mono<NoteCredit> findByNumeroNoteCreditAndOrganizationId(String numeroNoteCredit, UUID organizationId);
    Mono<NoteCredit> findByIdNoteCreditAndOrganizationId(UUID idNoteCredit, UUID organizationId);
}
