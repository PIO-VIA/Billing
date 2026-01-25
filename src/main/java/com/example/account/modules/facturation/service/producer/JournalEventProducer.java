package com.example.account.modules.facturation.service.producer;
import com.example.account.modules.core.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String JOURNAL_CREATED_TOPIC = "journal-created";
    private static final String JOURNAL_UPDATED_TOPIC = "journal-updated";
    private static final String JOURNAL_DELETED_TOPIC = "journal-deleted";

    public void publishJournalCreated(Object journalResponse) {
        log.info("Publication de l'événement journal créé");
        kafkaProducerService.sendMessage(JOURNAL_CREATED_TOPIC, journalResponse);
    }

    public void publishJournalUpdated(Object journalResponse) {
        log.info("Publication de l'événement journal mis à jour");
        kafkaProducerService.sendMessage(JOURNAL_UPDATED_TOPIC, journalResponse);
    }

    public void publishJournalDeleted(UUID journalId) {
        log.info("Publication de l'événement journal supprimé: {}", journalId);
        kafkaProducerService.sendMessage(JOURNAL_DELETED_TOPIC, journalId.toString(), journalId);
    }
}
