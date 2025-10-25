package com.example.account.service;

import com.example.account.model.entity.MouvementStock;
import com.example.account.repository.MouvementStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MouvementStockServiceTest {

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @InjectMocks
    private MouvementStockService mouvementStockService;

    private MouvementStock mouvement;
    private UUID mouvementId;
    private UUID produitId;
    private UUID stockId;

    @BeforeEach
    void setUp() {
        mouvementId = UUID.randomUUID();
        produitId = UUID.randomUUID();
        stockId = UUID.randomUUID();

        mouvement = MouvementStock.builder()
                .idMouvement(mouvementId)
                .numeroMouvement("MVT-2025-001")
                .typeMouvement("ENTREE")
                .dateMouvement(LocalDateTime.now())
                .idProduit(produitId)
                .nomProduit("Produit Test")
                .referenceProduit("REF001")
                .idStock(stockId)
                .quantite(new BigDecimal("50"))
                .quantiteAvant(new BigDecimal("100"))
                .quantiteApres(new BigDecimal("150"))
                .uniteMesure("UNITE")
                .coutUnitaire(new BigDecimal("25.00"))
                .valeurMouvement(new BigDecimal("1250.00"))
                .emplacementSource("A-01-01")
                .emplacementDestination("A-01-02")
                .documentReference("FAC-2025-001")
                .typeDocument("FACTURE")
                .motif("Réapprovisionnement")
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .validated(false)
                .build();
    }

    @Test
    void getMouvementById_shouldReturnMouvement() {
        // Given
        when(mouvementStockRepository.findById(mouvementId)).thenReturn(Optional.of(mouvement));

        // When
        MouvementStock result = mouvementStockService.getMouvementById(mouvementId);

        // Then
        assertNotNull(result);
        assertEquals(mouvementId, result.getIdMouvement());
        assertEquals("MVT-2025-001", result.getNumeroMouvement());
        assertEquals("ENTREE", result.getTypeMouvement());
        assertEquals(new BigDecimal("50"), result.getQuantite());
        assertEquals(produitId, result.getIdProduit());
        assertFalse(result.getValidated());
        verify(mouvementStockRepository).findById(mouvementId);
    }

    @Test
    void getMouvementById_shouldThrowException_whenNotFound() {
        // Given
        when(mouvementStockRepository.findById(mouvementId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mouvementStockService.getMouvementById(mouvementId));

        assertEquals("Mouvement non trouvé: " + mouvementId, exception.getMessage());
        verify(mouvementStockRepository).findById(mouvementId);
    }

    @Test
    void getMouvementByNumero_shouldReturnMouvement() {
        // Given
        String numeroMouvement = "MVT-2025-001";
        when(mouvementStockRepository.findByNumeroMouvement(numeroMouvement)).thenReturn(Optional.of(mouvement));

        // When
        MouvementStock result = mouvementStockService.getMouvementByNumero(numeroMouvement);

        // Then
        assertNotNull(result);
        assertEquals(numeroMouvement, result.getNumeroMouvement());
        assertEquals("ENTREE", result.getTypeMouvement());
        verify(mouvementStockRepository).findByNumeroMouvement(numeroMouvement);
    }

    @Test
    void getMouvementByNumero_shouldThrowException_whenNotFound() {
        // Given
        String numeroMouvement = "MVT-2025-999";
        when(mouvementStockRepository.findByNumeroMouvement(numeroMouvement)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mouvementStockService.getMouvementByNumero(numeroMouvement));

        assertEquals("Mouvement non trouvé: " + numeroMouvement, exception.getMessage());
        verify(mouvementStockRepository).findByNumeroMouvement(numeroMouvement);
    }

    @Test
    void getMouvementsByProduit_shouldReturnMouvementList() {
        // Given
        MouvementStock mouvement2 = MouvementStock.builder()
                .idMouvement(UUID.randomUUID())
                .numeroMouvement("MVT-2025-002")
                .typeMouvement("SORTIE")
                .idProduit(produitId)
                .quantite(new BigDecimal("20"))
                .build();

        List<MouvementStock> mouvements = Arrays.asList(mouvement, mouvement2);
        when(mouvementStockRepository.findByIdProduit(produitId)).thenReturn(mouvements);

        // When
        List<MouvementStock> result = mouvementStockService.getMouvementsByProduit(produitId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ENTREE", result.get(0).getTypeMouvement());
        assertEquals("SORTIE", result.get(1).getTypeMouvement());
        verify(mouvementStockRepository).findByIdProduit(produitId);
    }

    @Test
    void getMouvementsByType_shouldReturnMouvementList() {
        // Given
        String typeMouvement = "ENTREE";
        List<MouvementStock> mouvements = Arrays.asList(mouvement);
        when(mouvementStockRepository.findByTypeMouvement(typeMouvement)).thenReturn(mouvements);

        // When
        List<MouvementStock> result = mouvementStockService.getMouvementsByType(typeMouvement);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(typeMouvement, result.get(0).getTypeMouvement());
        verify(mouvementStockRepository).findByTypeMouvement(typeMouvement);
    }

    @Test
    void getMouvementsByPeriode_shouldReturnMouvementList() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<MouvementStock> mouvements = Arrays.asList(mouvement);
        when(mouvementStockRepository.findByDateMouvementBetween(startDate, endDate)).thenReturn(mouvements);

        // When
        List<MouvementStock> result = mouvementStockService.getMouvementsByPeriode(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mouvementStockRepository).findByDateMouvementBetween(startDate, endDate);
    }

    @Test
    void getHistoriqueProduit_shouldReturnMouvementList() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<MouvementStock> mouvements = Arrays.asList(mouvement);
        when(mouvementStockRepository.findHistoriqueProduit(produitId, startDate, endDate)).thenReturn(mouvements);

        // When
        List<MouvementStock> result = mouvementStockService.getHistoriqueProduit(produitId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produitId, result.get(0).getIdProduit());
        verify(mouvementStockRepository).findHistoriqueProduit(produitId, startDate, endDate);
    }

    @Test
    void getMouvementsNonValides_shouldReturnMouvementList() {
        // Given
        MouvementStock mouvementNonValide = MouvementStock.builder()
                .idMouvement(UUID.randomUUID())
                .numeroMouvement("MVT-2025-003")
                .validated(false)
                .build();

        List<MouvementStock> mouvements = Arrays.asList(mouvement, mouvementNonValide);
        when(mouvementStockRepository.findMouvementsNonValides()).thenReturn(mouvements);

        // When
        List<MouvementStock> result = mouvementStockService.getMouvementsNonValides();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(m -> assertFalse(m.getValidated()));
        verify(mouvementStockRepository).findMouvementsNonValides();
    }

    @Test
    void validerMouvement_shouldValidateMouvement() {
        // Given
        String validatedBy = "admin";
        when(mouvementStockRepository.findById(mouvementId)).thenReturn(Optional.of(mouvement));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenReturn(mouvement);

        // When
        MouvementStock result = mouvementStockService.validerMouvement(mouvementId, validatedBy);

        // Then
        assertNotNull(result);
        assertTrue(result.getValidated());
        assertEquals(validatedBy, result.getValidatedBy());
        assertNotNull(result.getValidatedAt());
        verify(mouvementStockRepository).findById(mouvementId);
        verify(mouvementStockRepository).save(mouvement);
    }

    @Test
    void validerMouvement_shouldThrowException_whenAlreadyValidated() {
        // Given
        mouvement.setValidated(true);
        mouvement.setValidatedBy("admin");
        mouvement.setValidatedAt(LocalDateTime.now());
        when(mouvementStockRepository.findById(mouvementId)).thenReturn(Optional.of(mouvement));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> mouvementStockService.validerMouvement(mouvementId, "user"));

        assertEquals("Le mouvement est déjà validé", exception.getMessage());
        verify(mouvementStockRepository).findById(mouvementId);
        verify(mouvementStockRepository, never()).save(any(MouvementStock.class));
    }

    @Test
    void validerMouvement_shouldThrowException_whenMouvementNotFound() {
        // Given
        when(mouvementStockRepository.findById(mouvementId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mouvementStockService.validerMouvement(mouvementId, "admin"));

        assertEquals("Mouvement non trouvé: " + mouvementId, exception.getMessage());
        verify(mouvementStockRepository).findById(mouvementId);
        verify(mouvementStockRepository, never()).save(any(MouvementStock.class));
    }
}
