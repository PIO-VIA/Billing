package com.example.account.service;

import com.example.account.dto.request.JournalCreateRequest;
import com.example.account.dto.request.JournalUpdateRequest;
import com.example.account.dto.response.JournalResponse;
import com.example.account.mapper.JournalMapper;
import com.example.account.model.entity.Journal;
import com.example.account.repository.JournalRepository;
import com.example.account.service.producer.JournalEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private JournalEventProducer journalEventProducer;

    @Mock
    private JournalMapper journalMapper;

    @InjectMocks
    private JournalService journalService;

    private Journal journal;
    private JournalResponse journalResponse;
    private JournalCreateRequest journalCreateRequest;
    private JournalUpdateRequest journalUpdateRequest;
    private UUID journalId;

    @BeforeEach
    void setUp() {
        journalId = UUID.randomUUID();

        journal = Journal.builder()
                .idJournal(journalId)
                .nomJournal("Journal Ventes")
                .type("Vente")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        journalResponse = JournalResponse.builder()
                .idJournal(journalId)
                .nomJournal("Journal Ventes")
                .type("Vente")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        journalCreateRequest = JournalCreateRequest.builder()
                .nomJournal("Journal Ventes")
                .type("Vente")
                .build();

        journalUpdateRequest = JournalUpdateRequest.builder()
                .nomJournal("Journal Ventes Modifié")
                .type("Vente")
                .build();
    }

    @Test
    void createJournal_shouldCreateJournalSuccessfully() {
        // Given
        when(journalRepository.existsByNomJournal(journalCreateRequest.getNomJournal())).thenReturn(false);
        when(journalMapper.toEntity(journalCreateRequest)).thenReturn(journal);
        when(journalRepository.save(journal)).thenReturn(journal);
        when(journalMapper.toResponse(journal)).thenReturn(journalResponse);
        doNothing().when(journalEventProducer).publishJournalCreated(journalResponse);

        // When
        JournalResponse result = journalService.createJournal(journalCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals(journalResponse.getIdJournal(), result.getIdJournal());
        assertEquals(journalResponse.getNomJournal(), result.getNomJournal());
        verify(journalRepository).existsByNomJournal(journalCreateRequest.getNomJournal());
        verify(journalRepository).save(journal);
        verify(journalEventProducer).publishJournalCreated(journalResponse);
    }

    @Test
    void createJournal_shouldThrowException_whenNomJournalAlreadyExists() {
        // Given
        when(journalRepository.existsByNomJournal(journalCreateRequest.getNomJournal())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> journalService.createJournal(journalCreateRequest));

        assertEquals("Un journal avec ce nom existe déjà", exception.getMessage());
        verify(journalRepository).existsByNomJournal(journalCreateRequest.getNomJournal());
        verify(journalRepository, never()).save(any());
        verify(journalEventProducer, never()).publishJournalCreated(any());
    }

    @Test
    void updateJournal_shouldUpdateJournalSuccessfully() {
        // Given
        when(journalRepository.findById(journalId)).thenReturn(Optional.of(journal));
        doNothing().when(journalMapper).updateEntityFromRequest(journalUpdateRequest, journal);
        when(journalRepository.save(journal)).thenReturn(journal);
        when(journalMapper.toResponse(journal)).thenReturn(journalResponse);
        doNothing().when(journalEventProducer).publishJournalUpdated(journalResponse);

        // When
        JournalResponse result = journalService.updateJournal(journalId, journalUpdateRequest);

        // Then
        assertNotNull(result);
        assertEquals(journalResponse.getIdJournal(), result.getIdJournal());
        verify(journalRepository).findById(journalId);
        verify(journalMapper).updateEntityFromRequest(journalUpdateRequest, journal);
        verify(journalRepository).save(journal);
        verify(journalEventProducer).publishJournalUpdated(journalResponse);
    }

    @Test
    void updateJournal_shouldThrowException_whenJournalNotFound() {
        // Given
        when(journalRepository.findById(journalId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> journalService.updateJournal(journalId, journalUpdateRequest));

        assertEquals("Journal non trouvé: " + journalId, exception.getMessage());
        verify(journalRepository).findById(journalId);
        verify(journalRepository, never()).save(any());
        verify(journalEventProducer, never()).publishJournalUpdated(any());
    }

    @Test
    void getJournalById_shouldReturnJournal() {
        // Given
        when(journalRepository.findById(journalId)).thenReturn(Optional.of(journal));
        when(journalMapper.toResponse(journal)).thenReturn(journalResponse);

        // When
        JournalResponse result = journalService.getJournalById(journalId);

        // Then
        assertNotNull(result);
        assertEquals(journalResponse.getIdJournal(), result.getIdJournal());
        assertEquals(journalResponse.getNomJournal(), result.getNomJournal());
        verify(journalRepository).findById(journalId);
        verify(journalMapper).toResponse(journal);
    }

    @Test
    void getJournalById_shouldThrowException_whenNotFound() {
        // Given
        when(journalRepository.findById(journalId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> journalService.getJournalById(journalId));

        assertEquals("Journal non trouvé: " + journalId, exception.getMessage());
        verify(journalRepository).findById(journalId);
    }

    @Test
    void getJournalByNom_shouldReturnJournal() {
        // Given
        String nomJournal = "Journal Ventes";
        when(journalRepository.findByNomJournal(nomJournal)).thenReturn(Optional.of(journal));
        when(journalMapper.toResponse(journal)).thenReturn(journalResponse);

        // When
        JournalResponse result = journalService.getJournalByNom(nomJournal);

        // Then
        assertNotNull(result);
        assertEquals(journalResponse.getNomJournal(), result.getNomJournal());
        verify(journalRepository).findByNomJournal(nomJournal);
        verify(journalMapper).toResponse(journal);
    }

    @Test
    void getJournalByNom_shouldThrowException_whenNotFound() {
        // Given
        String nomJournal = "Journal Inexistant";
        when(journalRepository.findByNomJournal(nomJournal)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> journalService.getJournalByNom(nomJournal));

        assertEquals("Journal non trouvé avec nom: " + nomJournal, exception.getMessage());
        verify(journalRepository).findByNomJournal(nomJournal);
    }

    @Test
    void getAllJournals_shouldReturnJournalList() {
        // Given
        List<Journal> journals = Arrays.asList(journal);
        List<JournalResponse> journalResponses = Arrays.asList(journalResponse);
        when(journalRepository.findAll()).thenReturn(journals);
        when(journalMapper.toResponseList(journals)).thenReturn(journalResponses);

        // When
        List<JournalResponse> result = journalService.getAllJournals();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(journalResponse.getIdJournal(), result.get(0).getIdJournal());
        verify(journalRepository).findAll();
        verify(journalMapper).toResponseList(journals);
    }

    @Test
    void getAllJournals_withPageable_shouldReturnPagedJournals() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Journal> journalPage = new PageImpl<>(Arrays.asList(journal));
        when(journalRepository.findAll(pageable)).thenReturn(journalPage);
        when(journalMapper.toResponse(journal)).thenReturn(journalResponse);

        // When
        Page<JournalResponse> result = journalService.getAllJournals(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(journalResponse.getIdJournal(), result.getContent().get(0).getIdJournal());
        verify(journalRepository).findAll(pageable);
    }

    @Test
    void getJournalsByType_shouldReturnJournalList() {
        // Given
        String type = "Vente";
        List<Journal> journals = Arrays.asList(journal);
        List<JournalResponse> journalResponses = Arrays.asList(journalResponse);
        when(journalRepository.findByType(type)).thenReturn(journals);
        when(journalMapper.toResponseList(journals)).thenReturn(journalResponses);

        // When
        List<JournalResponse> result = journalService.getJournalsByType(type);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(journalResponse.getType(), result.get(0).getType());
        verify(journalRepository).findByType(type);
        verify(journalMapper).toResponseList(journals);
    }

    @Test
    void searchJournalsByNom_shouldReturnJournalList() {
        // Given
        String nomJournal = "Ventes";
        List<Journal> journals = Arrays.asList(journal);
        List<JournalResponse> journalResponses = Arrays.asList(journalResponse);
        when(journalRepository.findByNomJournalContaining(nomJournal)).thenReturn(journals);
        when(journalMapper.toResponseList(journals)).thenReturn(journalResponses);

        // When
        List<JournalResponse> result = journalService.searchJournalsByNom(nomJournal);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(journalRepository).findByNomJournalContaining(nomJournal);
        verify(journalMapper).toResponseList(journals);
    }

    @Test
    void deleteJournal_shouldDeleteSuccessfully() {
        // Given
        when(journalRepository.existsById(journalId)).thenReturn(true);
        doNothing().when(journalRepository).deleteById(journalId);
        doNothing().when(journalEventProducer).publishJournalDeleted(journalId);

        // When
        journalService.deleteJournal(journalId);

        // Then
        verify(journalRepository).existsById(journalId);
        verify(journalRepository).deleteById(journalId);
        verify(journalEventProducer).publishJournalDeleted(journalId);
    }

    @Test
    void deleteJournal_shouldThrowException_whenJournalNotFound() {
        // Given
        when(journalRepository.existsById(journalId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> journalService.deleteJournal(journalId));

        assertEquals("Journal non trouvé: " + journalId, exception.getMessage());
        verify(journalRepository).existsById(journalId);
        verify(journalRepository, never()).deleteById(any());
        verify(journalEventProducer, never()).publishJournalDeleted(any());
    }

    @Test
    void countByType_shouldReturnCount() {
        // Given
        String type = "Vente";
        Long expectedCount = 5L;
        when(journalRepository.countByType(type)).thenReturn(expectedCount);

        // When
        Long result = journalService.countByType(type);

        // Then
        assertEquals(expectedCount, result);
        verify(journalRepository).countByType(type);
    }
}
