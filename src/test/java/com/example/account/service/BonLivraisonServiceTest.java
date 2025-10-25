package com.example.account.service;

import com.example.account.model.entity.BonLivraison;
import com.example.account.repository.BonLivraisonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonLivraisonServiceTest {

    @Mock
    private BonLivraisonRepository bonLivraisonRepository;

    @InjectMocks
    private BonLivraisonService bonLivraisonService;

    private BonLivraison bonLivraison;
    private UUID bonLivraisonId;
    private UUID clientId;
    private UUID factureId;

    @BeforeEach
    void setUp() {
        bonLivraisonId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        factureId = UUID.randomUUID();

        bonLivraison = BonLivraison.builder()
                .idBonLivraison(bonLivraisonId)
                .numeroBonLivraison("BL-2025-001")
                .dateLivraison(LocalDate.now().plusDays(2))
                .heureLivraison("14:00")
                .idClient(clientId)
                .nomClient("Client Test")
                .idFacture(factureId)
                .numeroFacture("FAC-2025-001")
                .adresseLivraison("123 Rue de Test")
                .villeLivraison("Paris")
                .codePostalLivraison("75001")
                .paysLivraison("France")
                .transporteur("DHL")
                .numeroSuivi("1234567890")
                .statut("EN_PREPARATION")
                .nombreColis(2)
                .poidsTotal("15.5 kg")
                .livraisonEffectuee(false)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getBonLivraisonById_shouldReturnBonLivraison() {
        // Given
        when(bonLivraisonRepository.findById(bonLivraisonId)).thenReturn(Optional.of(bonLivraison));

        // When
        BonLivraison result = bonLivraisonService.getBonLivraisonById(bonLivraisonId);

        // Then
        assertNotNull(result);
        assertEquals(bonLivraisonId, result.getIdBonLivraison());
        assertEquals("BL-2025-001", result.getNumeroBonLivraison());
        assertEquals(clientId, result.getIdClient());
        assertEquals("EN_PREPARATION", result.getStatut());
        assertFalse(result.getLivraisonEffectuee());
        verify(bonLivraisonRepository).findById(bonLivraisonId);
    }

    @Test
    void getBonLivraisonById_shouldThrowException_whenNotFound() {
        // Given
        when(bonLivraisonRepository.findById(bonLivraisonId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bonLivraisonService.getBonLivraisonById(bonLivraisonId));

        assertEquals("Bon de livraison non trouvé: " + bonLivraisonId, exception.getMessage());
        verify(bonLivraisonRepository).findById(bonLivraisonId);
    }

    @Test
    void getBonLivraisonByNumero_shouldReturnBonLivraison() {
        // Given
        String numero = "BL-2025-001";
        when(bonLivraisonRepository.findByNumeroBonLivraison(numero)).thenReturn(Optional.of(bonLivraison));

        // When
        BonLivraison result = bonLivraisonService.getBonLivraisonByNumero(numero);

        // Then
        assertNotNull(result);
        assertEquals(numero, result.getNumeroBonLivraison());
        verify(bonLivraisonRepository).findByNumeroBonLivraison(numero);
    }

    @Test
    void getBonLivraisonByNumero_shouldThrowException_whenNotFound() {
        // Given
        String numero = "BL-2025-999";
        when(bonLivraisonRepository.findByNumeroBonLivraison(numero)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bonLivraisonService.getBonLivraisonByNumero(numero));

        assertEquals("Bon de livraison non trouvé: " + numero, exception.getMessage());
        verify(bonLivraisonRepository).findByNumeroBonLivraison(numero);
    }

    @Test
    void getBonLivraisonsByClient_shouldReturnBonLivraisonList() {
        // Given
        BonLivraison bonLivraison2 = BonLivraison.builder()
                .idBonLivraison(UUID.randomUUID())
                .numeroBonLivraison("BL-2025-002")
                .idClient(clientId)
                .build();

        List<BonLivraison> bonLivraisons = Arrays.asList(bonLivraison, bonLivraison2);
        when(bonLivraisonRepository.findByIdClient(clientId)).thenReturn(bonLivraisons);

        // When
        List<BonLivraison> result = bonLivraisonService.getBonLivraisonsByClient(clientId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(bl -> assertEquals(clientId, bl.getIdClient()));
        verify(bonLivraisonRepository).findByIdClient(clientId);
    }

    @Test
    void getBonLivraisonsByFacture_shouldReturnBonLivraisonList() {
        // Given
        List<BonLivraison> bonLivraisons = Arrays.asList(bonLivraison);
        when(bonLivraisonRepository.findByIdFacture(factureId)).thenReturn(bonLivraisons);

        // When
        List<BonLivraison> result = bonLivraisonService.getBonLivraisonsByFacture(factureId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(factureId, result.get(0).getIdFacture());
        verify(bonLivraisonRepository).findByIdFacture(factureId);
    }

    @Test
    void getBonLivraisonsByStatut_shouldReturnBonLivraisonList() {
        // Given
        String statut = "EN_PREPARATION";
        List<BonLivraison> bonLivraisons = Arrays.asList(bonLivraison);
        when(bonLivraisonRepository.findByStatut(statut)).thenReturn(bonLivraisons);

        // When
        List<BonLivraison> result = bonLivraisonService.getBonLivraisonsByStatut(statut);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(statut, result.get(0).getStatut());
        verify(bonLivraisonRepository).findByStatut(statut);
    }

    @Test
    void getBonLivraisonsByPeriode_shouldReturnBonLivraisonList() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        List<BonLivraison> bonLivraisons = Arrays.asList(bonLivraison);
        when(bonLivraisonRepository.findByDateLivraisonBetween(startDate, endDate)).thenReturn(bonLivraisons);

        // When
        List<BonLivraison> result = bonLivraisonService.getBonLivraisonsByPeriode(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bonLivraisonRepository).findByDateLivraisonBetween(startDate, endDate);
    }

    @Test
    void getLivraisonsEnRetard_shouldReturnBonLivraisonList() {
        // Given
        BonLivraison bonLivraisonRetard = BonLivraison.builder()
                .idBonLivraison(UUID.randomUUID())
                .numeroBonLivraison("BL-2025-003")
                .dateLivraison(LocalDate.now().minusDays(2))
                .livraisonEffectuee(false)
                .build();

        List<BonLivraison> bonLivraisons = Arrays.asList(bonLivraisonRetard);
        when(bonLivraisonRepository.findLivraisonsEnRetard(any(LocalDate.class))).thenReturn(bonLivraisons);

        // When
        List<BonLivraison> result = bonLivraisonService.getLivraisonsEnRetard();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getLivraisonEffectuee());
        verify(bonLivraisonRepository).findLivraisonsEnRetard(any(LocalDate.class));
    }

    @Test
    void marquerCommeEffectuee_shouldMarkAsDelivered() {
        // Given
        String signature = "John Doe";
        when(bonLivraisonRepository.findById(bonLivraisonId)).thenReturn(Optional.of(bonLivraison));
        when(bonLivraisonRepository.save(any(BonLivraison.class))).thenReturn(bonLivraison);

        // When
        BonLivraison result = bonLivraisonService.marquerCommeEffectuee(bonLivraisonId, signature);

        // Then
        assertNotNull(result);
        assertTrue(result.getLivraisonEffectuee());
        assertEquals("LIVRE", result.getStatut());
        assertEquals(signature, result.getSignatureClient());
        assertNotNull(result.getDateLivraisonEffective());
        assertNotNull(result.getDateSignature());
        assertNotNull(result.getUpdatedAt());
        verify(bonLivraisonRepository).findById(bonLivraisonId);
        verify(bonLivraisonRepository).save(bonLivraison);
    }

    @Test
    void marquerCommeEffectuee_shouldThrowException_whenAlreadyDelivered() {
        // Given
        bonLivraison.setLivraisonEffectuee(true);
        bonLivraison.setDateLivraisonEffective(LocalDateTime.now());
        when(bonLivraisonRepository.findById(bonLivraisonId)).thenReturn(Optional.of(bonLivraison));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bonLivraisonService.marquerCommeEffectuee(bonLivraisonId, "Signature"));

        assertEquals("La livraison a déjà été effectuée", exception.getMessage());
        verify(bonLivraisonRepository).findById(bonLivraisonId);
        verify(bonLivraisonRepository, never()).save(any(BonLivraison.class));
    }

    @Test
    void updateStatut_shouldUpdateStatut() {
        // Given
        String nouveauStatut = "EN_TRANSIT";
        when(bonLivraisonRepository.findById(bonLivraisonId)).thenReturn(Optional.of(bonLivraison));
        when(bonLivraisonRepository.save(any(BonLivraison.class))).thenReturn(bonLivraison);

        // When
        BonLivraison result = bonLivraisonService.updateStatut(bonLivraisonId, nouveauStatut);

        // Then
        assertNotNull(result);
        assertEquals(nouveauStatut, result.getStatut());
        assertNotNull(result.getUpdatedAt());
        verify(bonLivraisonRepository).findById(bonLivraisonId);
        verify(bonLivraisonRepository).save(bonLivraison);
    }

    @Test
    void updateStatut_shouldThrowException_whenBonLivraisonNotFound() {
        // Given
        when(bonLivraisonRepository.findById(bonLivraisonId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bonLivraisonService.updateStatut(bonLivraisonId, "EN_TRANSIT"));

        assertEquals("Bon de livraison non trouvé: " + bonLivraisonId, exception.getMessage());
        verify(bonLivraisonRepository).findById(bonLivraisonId);
        verify(bonLivraisonRepository, never()).save(any(BonLivraison.class));
    }
}
