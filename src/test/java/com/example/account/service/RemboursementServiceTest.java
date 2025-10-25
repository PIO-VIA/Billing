package com.example.account.service;

import com.example.account.dto.request.RemboursementCreateRequest;
import com.example.account.dto.request.RemboursementUpdateRequest;
import com.example.account.dto.response.RemboursementResponse;
import com.example.account.mapper.RemboursementMapper;
import com.example.account.model.entity.Remboursement;
import com.example.account.repository.RemboursementRepository;
import com.example.account.service.producer.RemboursementEventProducer;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemboursementServiceTest {

    @Mock
    private RemboursementRepository remboursementRepository;

    @Mock
    private RemboursementEventProducer remboursementEventProducer;

    @Mock
    private RemboursementMapper remboursementMapper;

    @InjectMocks
    private RemboursementService remboursementService;

    private Remboursement remboursement;
    private RemboursementResponse remboursementResponse;
    private RemboursementCreateRequest remboursementCreateRequest;
    private RemboursementUpdateRequest remboursementUpdateRequest;
    private UUID remboursementId;
    private UUID clientId;
    private UUID factureId;

    @BeforeEach
    void setUp() {
        remboursementId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        factureId = UUID.randomUUID();

        remboursement = Remboursement.builder()
                .idRemboursement(remboursementId)
                .dateFacturation(LocalDate.now())
                .dateComptable(LocalDate.now())
                .referencePaiement("REF001")
                .banqueDestination("BNP Paribas")
                .dateEcheance(LocalDate.now().plusDays(30))
                .montant(new BigDecimal("1000.00"))
                .devise("EUR")
                .tauxChange(BigDecimal.ONE)
                .motif("Remboursement facture")
                .numeroPiece("PIECE001")
                .statut("EN_ATTENTE")
                .idFacture(factureId)
                .idClient(clientId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        remboursementResponse = RemboursementResponse.builder()
                .idRemboursement(remboursementId)
                .dateFacturation(LocalDate.now())
                .dateComptable(LocalDate.now())
                .referencePaiement("REF001")
                .banqueDestination("BNP Paribas")
                .dateEcheance(LocalDate.now().plusDays(30))
                .montant(new BigDecimal("1000.00"))
                .devise("EUR")
                .tauxChange(BigDecimal.ONE)
                .motif("Remboursement facture")
                .numeroPiece("PIECE001")
                .statut("EN_ATTENTE")
                .idFacture(factureId)
                .idClient(clientId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        remboursementCreateRequest = RemboursementCreateRequest.builder()
                .dateFacturation(LocalDate.now())
                .dateComptable(LocalDate.now())
                .referencePaiement("REF001")
                .banqueDestination("BNP Paribas")
                .dateEcheance(LocalDate.now().plusDays(30))
                .montant(new BigDecimal("1000.00"))
                .devise("EUR")
                .tauxChange(BigDecimal.ONE)
                .motif("Remboursement facture")
                .numeroPiece("PIECE001")
                .idFacture(factureId)
                .idClient(clientId)
                .build();

        remboursementUpdateRequest = RemboursementUpdateRequest.builder()
                .montant(new BigDecimal("1200.00"))
                .statut("VALIDE")
                .build();
    }

    @Test
    void createRemboursement_shouldCreateRemboursementSuccessfully() {
        // Given
        when(remboursementMapper.toEntity(remboursementCreateRequest)).thenReturn(remboursement);
        when(remboursementRepository.save(remboursement)).thenReturn(remboursement);
        when(remboursementMapper.toResponse(remboursement)).thenReturn(remboursementResponse);
        doNothing().when(remboursementEventProducer).publishRemboursementCreated(remboursementResponse);

        // When
        RemboursementResponse result = remboursementService.createRemboursement(remboursementCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals(remboursementResponse.getIdRemboursement(), result.getIdRemboursement());
        assertEquals(remboursementResponse.getMontant(), result.getMontant());
        verify(remboursementRepository).save(remboursement);
        verify(remboursementEventProducer).publishRemboursementCreated(remboursementResponse);
    }

    @Test
    void updateRemboursement_shouldUpdateRemboursementSuccessfully() {
        // Given
        when(remboursementRepository.findById(remboursementId)).thenReturn(Optional.of(remboursement));
        doNothing().when(remboursementMapper).updateEntityFromRequest(remboursementUpdateRequest, remboursement);
        when(remboursementRepository.save(remboursement)).thenReturn(remboursement);
        when(remboursementMapper.toResponse(remboursement)).thenReturn(remboursementResponse);
        doNothing().when(remboursementEventProducer).publishRemboursementUpdated(remboursementResponse);

        // When
        RemboursementResponse result = remboursementService.updateRemboursement(remboursementId, remboursementUpdateRequest);

        // Then
        assertNotNull(result);
        assertEquals(remboursementResponse.getIdRemboursement(), result.getIdRemboursement());
        verify(remboursementRepository).findById(remboursementId);
        verify(remboursementMapper).updateEntityFromRequest(remboursementUpdateRequest, remboursement);
        verify(remboursementRepository).save(remboursement);
        verify(remboursementEventProducer).publishRemboursementUpdated(remboursementResponse);
    }

    @Test
    void updateRemboursement_shouldThrowException_whenRemboursementNotFound() {
        // Given
        when(remboursementRepository.findById(remboursementId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> remboursementService.updateRemboursement(remboursementId, remboursementUpdateRequest));

        assertEquals("Remboursement non trouvé: " + remboursementId, exception.getMessage());
        verify(remboursementRepository).findById(remboursementId);
        verify(remboursementRepository, never()).save(any());
        verify(remboursementEventProducer, never()).publishRemboursementUpdated(any());
    }

    @Test
    void getRemboursementById_shouldReturnRemboursement() {
        // Given
        when(remboursementRepository.findById(remboursementId)).thenReturn(Optional.of(remboursement));
        when(remboursementMapper.toResponse(remboursement)).thenReturn(remboursementResponse);

        // When
        RemboursementResponse result = remboursementService.getRemboursementById(remboursementId);

        // Then
        assertNotNull(result);
        assertEquals(remboursementResponse.getIdRemboursement(), result.getIdRemboursement());
        verify(remboursementRepository).findById(remboursementId);
        verify(remboursementMapper).toResponse(remboursement);
    }

    @Test
    void getRemboursementById_shouldThrowException_whenNotFound() {
        // Given
        when(remboursementRepository.findById(remboursementId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> remboursementService.getRemboursementById(remboursementId));

        assertEquals("Remboursement non trouvé: " + remboursementId, exception.getMessage());
        verify(remboursementRepository).findById(remboursementId);
    }

    @Test
    void getAllRemboursements_shouldReturnRemboursementList() {
        // Given
        List<Remboursement> remboursements = Arrays.asList(remboursement);
        List<RemboursementResponse> remboursementResponses = Arrays.asList(remboursementResponse);
        when(remboursementRepository.findAll()).thenReturn(remboursements);
        when(remboursementMapper.toResponseList(remboursements)).thenReturn(remboursementResponses);

        // When
        List<RemboursementResponse> result = remboursementService.getAllRemboursements();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(remboursementResponse.getIdRemboursement(), result.get(0).getIdRemboursement());
        verify(remboursementRepository).findAll();
        verify(remboursementMapper).toResponseList(remboursements);
    }

    @Test
    void getAllRemboursements_withPageable_shouldReturnPagedRemboursements() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Remboursement> remboursementPage = new PageImpl<>(Arrays.asList(remboursement));
        when(remboursementRepository.findAll(pageable)).thenReturn(remboursementPage);
        when(remboursementMapper.toResponse(remboursement)).thenReturn(remboursementResponse);

        // When
        Page<RemboursementResponse> result = remboursementService.getAllRemboursements(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(remboursementResponse.getIdRemboursement(), result.getContent().get(0).getIdRemboursement());
        verify(remboursementRepository).findAll(pageable);
    }

    @Test
    void getRemboursementsByClient_shouldReturnRemboursementList() {
        // Given
        List<Remboursement> remboursements = Arrays.asList(remboursement);
        List<RemboursementResponse> remboursementResponses = Arrays.asList(remboursementResponse);
        when(remboursementRepository.findByIdClient(clientId)).thenReturn(remboursements);
        when(remboursementMapper.toResponseList(remboursements)).thenReturn(remboursementResponses);

        // When
        List<RemboursementResponse> result = remboursementService.getRemboursementsByClient(clientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(remboursementRepository).findByIdClient(clientId);
        verify(remboursementMapper).toResponseList(remboursements);
    }

    @Test
    void getRemboursementsByStatut_shouldReturnRemboursementList() {
        // Given
        String statut = "EN_ATTENTE";
        List<Remboursement> remboursements = Arrays.asList(remboursement);
        List<RemboursementResponse> remboursementResponses = Arrays.asList(remboursementResponse);
        when(remboursementRepository.findByStatut(statut)).thenReturn(remboursements);
        when(remboursementMapper.toResponseList(remboursements)).thenReturn(remboursementResponses);

        // When
        List<RemboursementResponse> result = remboursementService.getRemboursementsByStatut(statut);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(remboursementRepository).findByStatut(statut);
        verify(remboursementMapper).toResponseList(remboursements);
    }

    @Test
    void deleteRemboursement_shouldDeleteSuccessfully() {
        // Given
        when(remboursementRepository.existsById(remboursementId)).thenReturn(true);
        doNothing().when(remboursementRepository).deleteById(remboursementId);
        doNothing().when(remboursementEventProducer).publishRemboursementDeleted(remboursementId);

        // When
        remboursementService.deleteRemboursement(remboursementId);

        // Then
        verify(remboursementRepository).existsById(remboursementId);
        verify(remboursementRepository).deleteById(remboursementId);
        verify(remboursementEventProducer).publishRemboursementDeleted(remboursementId);
    }

    @Test
    void deleteRemboursement_shouldThrowException_whenRemboursementNotFound() {
        // Given
        when(remboursementRepository.existsById(remboursementId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> remboursementService.deleteRemboursement(remboursementId));

        assertEquals("Remboursement non trouvé: " + remboursementId, exception.getMessage());
        verify(remboursementRepository).existsById(remboursementId);
        verify(remboursementRepository, never()).deleteById(any());
        verify(remboursementEventProducer, never()).publishRemboursementDeleted(any());
    }

    @Test
    void getTotalMontantByClient_shouldReturnTotal() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("5000.00");
        when(remboursementRepository.sumMontantByClient(clientId)).thenReturn(expectedTotal);

        // When
        BigDecimal result = remboursementService.getTotalMontantByClient(clientId);

        // Then
        assertEquals(expectedTotal, result);
        verify(remboursementRepository).sumMontantByClient(clientId);
    }

    @Test
    void getTotalMontantByClient_shouldReturnZero_whenNull() {
        // Given
        when(remboursementRepository.sumMontantByClient(clientId)).thenReturn(null);

        // When
        BigDecimal result = remboursementService.getTotalMontantByClient(clientId);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(remboursementRepository).sumMontantByClient(clientId);
    }

    @Test
    void countByStatut_shouldReturnCount() {
        // Given
        String statut = "EN_ATTENTE";
        Long expectedCount = 10L;
        when(remboursementRepository.countByStatut(statut)).thenReturn(expectedCount);

        // When
        Long result = remboursementService.countByStatut(statut);

        // Then
        assertEquals(expectedCount, result);
        verify(remboursementRepository).countByStatut(statut);
    }

    @Test
    void updateStatut_shouldUpdateStatutSuccessfully() {
        // Given
        String nouveauStatut = "VALIDE";
        when(remboursementRepository.findById(remboursementId)).thenReturn(Optional.of(remboursement));
        when(remboursementRepository.save(remboursement)).thenReturn(remboursement);
        when(remboursementMapper.toResponse(remboursement)).thenReturn(remboursementResponse);
        doNothing().when(remboursementEventProducer).publishRemboursementUpdated(remboursementResponse);

        // When
        RemboursementResponse result = remboursementService.updateStatut(remboursementId, nouveauStatut);

        // Then
        assertNotNull(result);
        verify(remboursementRepository).findById(remboursementId);
        verify(remboursementRepository).save(remboursement);
        verify(remboursementEventProducer).publishRemboursementUpdated(remboursementResponse);
    }
}
