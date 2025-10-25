package com.example.account.service;

import com.example.account.dto.request.BanqueCreateRequest;
import com.example.account.dto.request.BanqueUpdateRequest;
import com.example.account.dto.response.BanqueResponse;
import com.example.account.mapper.BanqueMapper;
import com.example.account.model.entity.Banque;
import com.example.account.repository.BanqueRepository;
import com.example.account.service.producer.BanqueEventProducer;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanqueServiceTest {

    @Mock
    private BanqueRepository banqueRepository;

    @Mock
    private BanqueEventProducer banqueEventProducer;

    @Mock
    private BanqueMapper banqueMapper;

    @InjectMocks
    private BanqueService banqueService;

    private Banque banque;
    private BanqueResponse banqueResponse;
    private BanqueCreateRequest banqueCreateRequest;
    private BanqueUpdateRequest banqueUpdateRequest;
    private UUID banqueId;

    @BeforeEach
    void setUp() {
        banqueId = UUID.randomUUID();

        banque = Banque.builder()
                .idBanque(banqueId)
                .numeroCompte("FR7630001007941234567890185")
                .banque("BNP Paribas")
                .build();

        banqueResponse = BanqueResponse.builder()
                .idBanque(banqueId)
                .numeroCompte("FR7630001007941234567890185")
                .banque("BNP Paribas")
                .build();

        banqueCreateRequest = BanqueCreateRequest.builder()
                .numeroCompte("FR7630001007941234567890185")
                .banque("BNP Paribas")
                .build();

        banqueUpdateRequest = BanqueUpdateRequest.builder()
                .numeroCompte("FR7630001007941234567890186")
                .banque("BNP Paribas Fortis")
                .build();
    }

    @Test
    void createBanque_shouldCreateBanqueSuccessfully() {
        // Given
        when(banqueRepository.existsByNumeroCompte(banqueCreateRequest.getNumeroCompte())).thenReturn(false);
        when(banqueMapper.toEntity(banqueCreateRequest)).thenReturn(banque);
        when(banqueRepository.save(banque)).thenReturn(banque);
        when(banqueMapper.toResponse(banque)).thenReturn(banqueResponse);
        doNothing().when(banqueEventProducer).publishBanqueCreated(banqueResponse);

        // When
        BanqueResponse result = banqueService.createBanque(banqueCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals(banqueResponse.getIdBanque(), result.getIdBanque());
        assertEquals(banqueResponse.getNumeroCompte(), result.getNumeroCompte());
        verify(banqueRepository).existsByNumeroCompte(banqueCreateRequest.getNumeroCompte());
        verify(banqueRepository).save(banque);
        verify(banqueEventProducer).publishBanqueCreated(banqueResponse);
    }

    @Test
    void createBanque_shouldThrowException_whenNumeroCompteAlreadyExists() {
        // Given
        when(banqueRepository.existsByNumeroCompte(banqueCreateRequest.getNumeroCompte())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> banqueService.createBanque(banqueCreateRequest));

        assertEquals("Un compte avec ce numéro existe déjà", exception.getMessage());
        verify(banqueRepository).existsByNumeroCompte(banqueCreateRequest.getNumeroCompte());
        verify(banqueRepository, never()).save(any());
        verify(banqueEventProducer, never()).publishBanqueCreated(any());
    }

    @Test
    void updateBanque_shouldUpdateBanqueSuccessfully() {
        // Given
        when(banqueRepository.findById(banqueId)).thenReturn(Optional.of(banque));
        when(banqueRepository.existsByNumeroCompte(banqueUpdateRequest.getNumeroCompte())).thenReturn(false);
        doNothing().when(banqueMapper).updateEntityFromRequest(banqueUpdateRequest, banque);
        when(banqueRepository.save(banque)).thenReturn(banque);
        when(banqueMapper.toResponse(banque)).thenReturn(banqueResponse);
        doNothing().when(banqueEventProducer).publishBanqueUpdated(banqueResponse);

        // When
        BanqueResponse result = banqueService.updateBanque(banqueId, banqueUpdateRequest);

        // Then
        assertNotNull(result);
        assertEquals(banqueResponse.getIdBanque(), result.getIdBanque());
        verify(banqueRepository).findById(banqueId);
        verify(banqueMapper).updateEntityFromRequest(banqueUpdateRequest, banque);
        verify(banqueRepository).save(banque);
        verify(banqueEventProducer).publishBanqueUpdated(banqueResponse);
    }

    @Test
    void updateBanque_shouldThrowException_whenBanqueNotFound() {
        // Given
        when(banqueRepository.findById(banqueId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> banqueService.updateBanque(banqueId, banqueUpdateRequest));

        assertEquals("Banque non trouvée: " + banqueId, exception.getMessage());
        verify(banqueRepository).findById(banqueId);
        verify(banqueRepository, never()).save(any());
        verify(banqueEventProducer, never()).publishBanqueUpdated(any());
    }

    @Test
    void updateBanque_shouldThrowException_whenNumeroCompteAlreadyExists() {
        // Given
        when(banqueRepository.findById(banqueId)).thenReturn(Optional.of(banque));
        when(banqueRepository.existsByNumeroCompte(banqueUpdateRequest.getNumeroCompte())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> banqueService.updateBanque(banqueId, banqueUpdateRequest));

        assertEquals("Un compte avec ce numéro existe déjà", exception.getMessage());
        verify(banqueRepository).findById(banqueId);
        verify(banqueRepository, never()).save(any());
        verify(banqueEventProducer, never()).publishBanqueUpdated(any());
    }

    @Test
    void getBanqueById_shouldReturnBanque() {
        // Given
        when(banqueRepository.findById(banqueId)).thenReturn(Optional.of(banque));
        when(banqueMapper.toResponse(banque)).thenReturn(banqueResponse);

        // When
        BanqueResponse result = banqueService.getBanqueById(banqueId);

        // Then
        assertNotNull(result);
        assertEquals(banqueResponse.getIdBanque(), result.getIdBanque());
        assertEquals(banqueResponse.getNumeroCompte(), result.getNumeroCompte());
        verify(banqueRepository).findById(banqueId);
        verify(banqueMapper).toResponse(banque);
    }

    @Test
    void getBanqueById_shouldThrowException_whenNotFound() {
        // Given
        when(banqueRepository.findById(banqueId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> banqueService.getBanqueById(banqueId));

        assertEquals("Banque non trouvée: " + banqueId, exception.getMessage());
        verify(banqueRepository).findById(banqueId);
    }

    @Test
    void getBanqueByNumeroCompte_shouldReturnBanque() {
        // Given
        String numeroCompte = "FR7630001007941234567890185";
        when(banqueRepository.findByNumeroCompte(numeroCompte)).thenReturn(Optional.of(banque));
        when(banqueMapper.toResponse(banque)).thenReturn(banqueResponse);

        // When
        BanqueResponse result = banqueService.getBanqueByNumeroCompte(numeroCompte);

        // Then
        assertNotNull(result);
        assertEquals(banqueResponse.getNumeroCompte(), result.getNumeroCompte());
        verify(banqueRepository).findByNumeroCompte(numeroCompte);
        verify(banqueMapper).toResponse(banque);
    }

    @Test
    void getBanqueByNumeroCompte_shouldThrowException_whenNotFound() {
        // Given
        String numeroCompte = "FR7630001007941234567890999";
        when(banqueRepository.findByNumeroCompte(numeroCompte)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> banqueService.getBanqueByNumeroCompte(numeroCompte));

        assertEquals("Banque non trouvée avec numéro de compte: " + numeroCompte, exception.getMessage());
        verify(banqueRepository).findByNumeroCompte(numeroCompte);
    }

    @Test
    void getAllBanques_shouldReturnBanqueList() {
        // Given
        List<Banque> banques = Arrays.asList(banque);
        List<BanqueResponse> banqueResponses = Arrays.asList(banqueResponse);
        when(banqueRepository.findAll()).thenReturn(banques);
        when(banqueMapper.toResponseList(banques)).thenReturn(banqueResponses);

        // When
        List<BanqueResponse> result = banqueService.getAllBanques();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(banqueResponse.getIdBanque(), result.get(0).getIdBanque());
        verify(banqueRepository).findAll();
        verify(banqueMapper).toResponseList(banques);
    }

    @Test
    void getAllBanques_withPageable_shouldReturnPagedBanques() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Banque> banquePage = new PageImpl<>(Arrays.asList(banque));
        when(banqueRepository.findAll(pageable)).thenReturn(banquePage);
        when(banqueMapper.toResponse(banque)).thenReturn(banqueResponse);

        // When
        Page<BanqueResponse> result = banqueService.getAllBanques(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(banqueResponse.getIdBanque(), result.getContent().get(0).getIdBanque());
        verify(banqueRepository).findAll(pageable);
    }

    @Test
    void getBanquesByName_shouldReturnBanqueList() {
        // Given
        String nomBanque = "BNP Paribas";
        List<Banque> banques = Arrays.asList(banque);
        List<BanqueResponse> banqueResponses = Arrays.asList(banqueResponse);
        when(banqueRepository.findByBanque(nomBanque)).thenReturn(banques);
        when(banqueMapper.toResponseList(banques)).thenReturn(banqueResponses);

        // When
        List<BanqueResponse> result = banqueService.getBanquesByName(nomBanque);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(banqueResponse.getBanque(), result.get(0).getBanque());
        verify(banqueRepository).findByBanque(nomBanque);
        verify(banqueMapper).toResponseList(banques);
    }

    @Test
    void searchBanquesByName_shouldReturnBanqueList() {
        // Given
        String nomBanque = "BNP";
        List<Banque> banques = Arrays.asList(banque);
        List<BanqueResponse> banqueResponses = Arrays.asList(banqueResponse);
        when(banqueRepository.findByBanqueContaining(nomBanque)).thenReturn(banques);
        when(banqueMapper.toResponseList(banques)).thenReturn(banqueResponses);

        // When
        List<BanqueResponse> result = banqueService.searchBanquesByName(nomBanque);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(banqueRepository).findByBanqueContaining(nomBanque);
        verify(banqueMapper).toResponseList(banques);
    }

    @Test
    void searchBanquesByNumeroCompte_shouldReturnBanqueList() {
        // Given
        String numeroCompte = "FR763";
        List<Banque> banques = Arrays.asList(banque);
        List<BanqueResponse> banqueResponses = Arrays.asList(banqueResponse);
        when(banqueRepository.findByNumeroCompteContaining(numeroCompte)).thenReturn(banques);
        when(banqueMapper.toResponseList(banques)).thenReturn(banqueResponses);

        // When
        List<BanqueResponse> result = banqueService.searchBanquesByNumeroCompte(numeroCompte);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(banqueRepository).findByNumeroCompteContaining(numeroCompte);
        verify(banqueMapper).toResponseList(banques);
    }

    @Test
    void deleteBanque_shouldDeleteSuccessfully() {
        // Given
        when(banqueRepository.existsById(banqueId)).thenReturn(true);
        doNothing().when(banqueRepository).deleteById(banqueId);
        doNothing().when(banqueEventProducer).publishBanqueDeleted(banqueId);

        // When
        banqueService.deleteBanque(banqueId);

        // Then
        verify(banqueRepository).existsById(banqueId);
        verify(banqueRepository).deleteById(banqueId);
        verify(banqueEventProducer).publishBanqueDeleted(banqueId);
    }

    @Test
    void deleteBanque_shouldThrowException_whenBanqueNotFound() {
        // Given
        when(banqueRepository.existsById(banqueId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> banqueService.deleteBanque(banqueId));

        assertEquals("Banque non trouvée: " + banqueId, exception.getMessage());
        verify(banqueRepository).existsById(banqueId);
        verify(banqueRepository, never()).deleteById(any());
        verify(banqueEventProducer, never()).publishBanqueDeleted(any());
    }
}
