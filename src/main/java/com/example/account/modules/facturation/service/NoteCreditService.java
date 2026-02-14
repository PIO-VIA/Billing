package com.example.account.modules.facturation.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.account.modules.facturation.dto.request.NoteCreditRequest;
import com.example.account.modules.facturation.dto.response.NoteCreditResponse;
import com.example.account.modules.facturation.mapper.NoteCreditMapper;
import com.example.account.modules.facturation.model.entity.LigneNoteCredit;
import com.example.account.modules.facturation.model.entity.NoteCredit;
import com.example.account.modules.facturation.model.enums.StatutNoteCredit;
import com.example.account.modules.facturation.repository.NoteCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteCreditService {

    private final NoteCreditRepository noteCreditRepository;
    private final NoteCreditMapper noteCreditMapper;

    @Transactional
    public Mono<NoteCreditResponse> createNoteCredit(NoteCreditRequest request) {
        log.info("Création d'une nouvelle note de crédit");
        NoteCredit entity = noteCreditMapper.toEntity(request);
        if (entity.getIdNoteCredit() == null) {
            entity.setIdNoteCredit(UUID.randomUUID());
        }
        return noteCreditRepository.save(entity)
                .map(noteCreditMapper::toResponse);
    }

    @Transactional
    public Mono<NoteCreditResponse> updateNoteCredit(UUID id, NoteCreditRequest request) {
        log.info("Mise à jour de la note de crédit: {}", id);
        return noteCreditRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Note de crédit non trouvée")))
                .flatMap(entity -> {
                    noteCreditMapper.updateEntityFromRequest(request, entity);
                    return noteCreditRepository.save(entity);
                })
                .map(noteCreditMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<NoteCreditResponse> getNoteCreditById(UUID id) {
        log.info("Récupération de la note de crédit ID: {}", id);
        // Temporarily ignoring orgId for reactive compatibility
        return noteCreditRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Note de crédit non trouvée")))
                .map(noteCreditMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<NoteCreditResponse> getAllNoteCredits() {
        log.info("Récupération de toutes les notes de crédit");
        return noteCreditRepository.findAll()
                .map(noteCreditMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteNoteCredit(UUID id) {
        log.info("Suppression de la note de crédit: {}", id);
        return noteCreditRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Note de crédit non trouvée")))
                .flatMap(noteCreditRepository::delete);
    }
}
