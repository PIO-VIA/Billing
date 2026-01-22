package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.JournalCreateRequest;
import com.example.account.modules.facturation.dto.request.JournalUpdateRequest;
import com.example.account.modules.facturation.dto.response.JournalResponse;
import com.example.account.modules.facturation.mapper.JournalMapper;
import com.example.account.modules.facturation.model.entity.Journal;
import com.example.account.modules.facturation.repository.JournalRepository;
import com.example.account.modules.facturation.service.producer.JournalEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final JournalRepository journalRepository;
    private final JournalEventProducer journalEventProducer;
    private final JournalMapper journalMapper;

    @Transactional
    public JournalResponse createJournal(JournalCreateRequest request) {
        log.info("Création d'un nouveau journal: {}", request.getNomJournal());

        // Vérifications
        if (journalRepository.existsByNomJournal(request.getNomJournal())) {
            throw new IllegalArgumentException("Un journal avec ce nom existe déjà");
        }

        // Créer et sauvegarder le journal
        Journal journal = journalMapper.toEntity(request);
        Journal savedJournal = journalRepository.save(journal);
        JournalResponse response = journalMapper.toResponse(savedJournal);

        // Publier l'événement
        journalEventProducer.publishJournalCreated(response);

        log.info("Journal créé avec succès: {}", savedJournal.getIdJournal());
        return response;
    }

    @Transactional
    public JournalResponse updateJournal(UUID journalId, JournalUpdateRequest request) {
        log.info("Mise à jour du journal: {}", journalId);

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new IllegalArgumentException("Journal non trouvé: " + journalId));

        // Mise à jour
        journalMapper.updateEntityFromRequest(request, journal);
        Journal updatedJournal = journalRepository.save(journal);
        JournalResponse response = journalMapper.toResponse(updatedJournal);

        // Publier l'événement
        journalEventProducer.publishJournalUpdated(response);

        log.info("Journal mis à jour avec succès: {}", journalId);
        return response;
    }

    @Transactional(readOnly = true)
    public JournalResponse getJournalById(UUID journalId) {
        log.info("Récupération du journal: {}", journalId);

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new IllegalArgumentException("Journal non trouvé: " + journalId));

        return journalMapper.toResponse(journal);
    }

    @Transactional(readOnly = true)
    public JournalResponse getJournalByNom(String nomJournal) {
        log.info("Récupération du journal par nom: {}", nomJournal);

        Journal journal = journalRepository.findByNomJournal(nomJournal)
                .orElseThrow(() -> new IllegalArgumentException("Journal non trouvé avec nom: " + nomJournal));

        return journalMapper.toResponse(journal);
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> getAllJournals() {
        log.info("Récupération de tous les journals");
        List<Journal> journals = journalRepository.findAll();
        return journalMapper.toResponseList(journals);
    }

    @Transactional(readOnly = true)
    public Page<JournalResponse> getAllJournals(Pageable pageable) {
        log.info("Récupération de tous les journals avec pagination");
        return journalRepository.findAll(pageable).map(journalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> getJournalsByType(String type) {
        log.info("Récupération des journals par type: {}", type);
        List<Journal> journals = journalRepository.findByType(type);
        return journalMapper.toResponseList(journals);
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> searchJournalsByNom(String nomJournal) {
        log.info("Recherche des journals par nom: {}", nomJournal);
        List<Journal> journals = journalRepository.findByNomJournalContaining(nomJournal);
        return journalMapper.toResponseList(journals);
    }

    @Transactional
    public void deleteJournal(UUID journalId) {
        log.info("Suppression du journal: {}", journalId);

        if (!journalRepository.existsById(journalId)) {
            throw new IllegalArgumentException("Journal non trouvé: " + journalId);
        }

        journalRepository.deleteById(journalId);

        // Publier l'événement
        journalEventProducer.publishJournalDeleted(journalId);

        log.info("Journal supprimé avec succès: {}", journalId);
    }

    @Transactional(readOnly = true)
    public Long countByType(String type) {
        return journalRepository.countByType(type);
    }
}
