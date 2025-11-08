package com.example.account.service;

import com.example.account.dto.response.StockResponse;
import com.example.account.model.entity.Stock;
import com.example.account.repository.StockRepository;
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
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock stock;
    private UUID stockId;
    private UUID produitId;

    @BeforeEach
    void setUp() {
        stockId = UUID.randomUUID();
        produitId = UUID.randomUUID();

        stock = Stock.builder()
                .idStock(stockId)
                .idProduit(produitId)
                .nomProduit("Produit Test")
                .referenceProduit("REF001")
                .quantite(new BigDecimal("100"))
                .quantiteReservee(new BigDecimal("10"))
                .quantiteDisponible(new BigDecimal("90"))
                .stockMinimum(new BigDecimal("20"))
                .stockMaximum(new BigDecimal("500"))
                .uniteMesure("UNITE")
                .emplacement("A-01-01")
                .zone("Zone A")
                .valeurStock(new BigDecimal("5000"))
                .coutMoyenUnitaire(new BigDecimal("50"))
                .statut("ACTIF")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getStockById_shouldReturnStock() {
        // Given
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));

        // When
        StockResponse result = stockService.getStockById(stockId);

        // Then
        assertNotNull(result);
        assertEquals(stockId, result.getIdStock());
        assertEquals(produitId, result.getIdProduit());
        assertEquals("Produit Test", result.getNomProduit());
        assertEquals(new BigDecimal("100"), result.getQuantite());
        assertEquals(new BigDecimal("90"), result.getQuantiteDisponible());
        assertEquals("ACTIF", result.getStatut());
        assertFalse(result.getAlerteStockBas());
        verify(stockRepository).findById(stockId);
    }

    @Test
    void getStockById_shouldThrowException_whenNotFound() {
        // Given
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> stockService.getStockById(stockId));

        assertEquals("Stock non trouvé: " + stockId, exception.getMessage());
        verify(stockRepository).findById(stockId);
    }

    @Test
    void getStockByProduit_shouldReturnStock() {
        // Given
        when(stockRepository.findByIdProduit(produitId)).thenReturn(Optional.of(stock));

        // When
        StockResponse result = stockService.getStockByProduit(produitId);

        // Then
        assertNotNull(result);
        assertEquals(produitId, result.getIdProduit());
        assertEquals("REF001", result.getReferenceProduit());
        verify(stockRepository).findByIdProduit(produitId);
    }

    @Test
    void getStockByProduit_shouldThrowException_whenNotFound() {
        // Given
        when(stockRepository.findByIdProduit(produitId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> stockService.getStockByProduit(produitId));

        assertEquals("Stock non trouvé pour le produit: " + produitId, exception.getMessage());
        verify(stockRepository).findByIdProduit(produitId);
    }

    @Test
    void getStocksSousMinimum_shouldReturnStockList() {
        // Given
        Stock stockBas = Stock.builder()
                .idStock(UUID.randomUUID())
                .quantite(new BigDecimal("5"))
                .stockMinimum(new BigDecimal("20"))
                .build();

        List<Stock> stocksSousMinimum = Arrays.asList(stockBas);
        when(stockRepository.findStocksSousMinimum()).thenReturn(stocksSousMinimum);

        // When
        List<Stock> result = stockService.getStocksSousMinimum();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stockRepository).findStocksSousMinimum();
    }

    @Test
    void getValeurTotaleStock_shouldReturnTotal() {
        // Given
        BigDecimal valeurTotale = new BigDecimal("150000.00");
        when(stockRepository.calculerValeurTotaleStock()).thenReturn(valeurTotale);

        // When
        BigDecimal result = stockService.getValeurTotaleStock();

        // Then
        assertEquals(valeurTotale, result);
        verify(stockRepository).calculerValeurTotaleStock();
    }

    @Test
    void stockResponse_shouldHaveAlerteStockBas_whenQuantityBelowMinimum() {
        // Given
        stock.setQuantite(new BigDecimal("15"));
        stock.setStockMinimum(new BigDecimal("20"));
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));

        // When
        StockResponse result = stockService.getStockById(stockId);

        // Then
        assertTrue(result.getAlerteStockBas());
    }
}
