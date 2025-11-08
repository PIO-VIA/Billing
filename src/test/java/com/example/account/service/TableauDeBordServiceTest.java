package com.example.account.service;

import com.example.account.dto.response.TableauDeBordResponse;
import com.example.account.model.entity.Stock;
import com.example.account.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableauDeBordServiceTest {

    @Mock
    private FactureRepository factureRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private BonCommandeRepository bonCommandeRepository;

    @Mock
    private BonAchatRepository bonAchatRepository;

    @Mock
    private PlanTresorerieRepository planTresorerieRepository;

    @InjectMocks
    private TableauDeBordService tableauDeBordService;

    @BeforeEach
    void setUp() {
        // Setup default mock behavior
    }

    @Test
    void getTableauDeBord_shouldReturnDashboardData() {
        // Given
        when(factureRepository.sumMontantByDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("50000.00"));
        when(factureRepository.count()).thenReturn(100L);
        when(factureRepository.countByStatut("PAYEE")).thenReturn(60L);
        when(factureRepository.countByStatut("EN_ATTENTE")).thenReturn(30L);
        when(factureRepository.sumMontantByStatut("EN_ATTENTE")).thenReturn(new BigDecimal("15000.00"));

        when(clientRepository.count()).thenReturn(50L);
        when(produitRepository.count()).thenReturn(200L);
        when(produitRepository.countByActive(true)).thenReturn(180L);

        List<Stock> stocksSousMinimum = Arrays.asList(
                Stock.builder().build(),
                Stock.builder().build()
        );
        when(stockRepository.findStocksSousMinimum()).thenReturn(stocksSousMinimum);
        when(stockRepository.calculerValeurTotaleStock()).thenReturn(new BigDecimal("75000.00"));

        when(bonCommandeRepository.count()).thenReturn(25L);
        when(bonAchatRepository.count()).thenReturn(20L);

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertNotNull(result);

        // Vérifier les indicateurs financiers
        assertEquals(new BigDecimal("50000.00"), result.getChiffreAffairesMois());
        assertEquals(new BigDecimal("50000.00"), result.getChiffreAffairesAnnee());

        // Vérifier les factures
        assertEquals(100L, result.getNombreFacturesEmises());
        assertEquals(60L, result.getNombreFacturesPayees());
        assertEquals(30L, result.getNombreFacturesEnAttente());
        assertEquals(new BigDecimal("15000.00"), result.getMontantFacturesEnAttente());

        // Vérifier les clients
        assertEquals(50L, result.getNombreClients());

        // Vérifier les produits
        assertEquals(200L, result.getNombreProduits());
        assertEquals(180L, result.getNombreProduitsActifs());
        assertEquals(2L, result.getNombreProduitsAlerteStock());

        // Vérifier le stock
        assertEquals(new BigDecimal("75000.00"), result.getValeurTotaleStock());

        // Vérifier les achats
        assertEquals(25L, result.getNombreBonsCommande());
        assertEquals(20L, result.getNombreBonsAchat());

        // Vérifier les listes
        assertNotNull(result.getTopProduits());
        assertNotNull(result.getTopClients());
        assertNotNull(result.getEvolutionCA12Mois());

        // Vérifier la date de génération
        assertEquals(LocalDate.now(), result.getDateGeneration());

        // Vérifier les appels aux repositories
        verify(factureRepository, times(2)).sumMontantByDateBetween(any(LocalDate.class), any(LocalDate.class));
        verify(factureRepository).count();
        verify(factureRepository, times(2)).countByStatut(anyString());
        verify(factureRepository).sumMontantByStatut("EN_ATTENTE");
        verify(clientRepository).count();
        verify(produitRepository).count();
        verify(produitRepository).countByActive(true);
        verify(stockRepository).findStocksSousMinimum();
        verify(stockRepository).calculerValeurTotaleStock();
        verify(bonCommandeRepository).count();
        verify(bonAchatRepository).count();
    }

    @Test
    void getTableauDeBord_shouldHandleNullValues() {
        // Given - All repositories return null for BigDecimal values
        when(factureRepository.sumMontantByDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(factureRepository.count()).thenReturn(0L);
        when(factureRepository.countByStatut(anyString())).thenReturn(0L);
        when(factureRepository.sumMontantByStatut(anyString())).thenReturn(null);

        when(clientRepository.count()).thenReturn(0L);
        when(produitRepository.count()).thenReturn(0L);
        when(produitRepository.countByActive(true)).thenReturn(0L);

        when(stockRepository.findStocksSousMinimum()).thenReturn(Arrays.asList());
        when(stockRepository.calculerValeurTotaleStock()).thenReturn(null);

        when(bonCommandeRepository.count()).thenReturn(0L);
        when(bonAchatRepository.count()).thenReturn(0L);

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertNotNull(result);

        // Vérifier que les valeurs nulles sont converties en ZERO
        assertEquals(BigDecimal.ZERO, result.getChiffreAffairesMois());
        assertEquals(BigDecimal.ZERO, result.getChiffreAffairesAnnee());
        assertEquals(BigDecimal.ZERO, result.getMontantFacturesEnAttente());
        assertEquals(BigDecimal.ZERO, result.getValeurTotaleStock());

        // Vérifier les compteurs
        assertEquals(0L, result.getNombreFacturesEmises());
        assertEquals(0L, result.getNombreFacturesPayees());
        assertEquals(0L, result.getNombreClients());
        assertEquals(0L, result.getNombreProduits());
        assertEquals(0L, result.getNombreProduitsAlerteStock());
    }

    @Test
    void getTableauDeBord_shouldCalculateCAForCurrentMonth() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        BigDecimal caMois = new BigDecimal("25000.00");

        when(factureRepository.sumMontantByDateBetween(eq(firstDayOfMonth), eq(today)))
                .thenReturn(caMois);
        when(factureRepository.sumMontantByDateBetween(any(LocalDate.class), eq(today)))
                .thenReturn(caMois);
        when(factureRepository.count()).thenReturn(10L);
        when(factureRepository.countByStatut(anyString())).thenReturn(0L);
        when(clientRepository.count()).thenReturn(10L);
        when(produitRepository.count()).thenReturn(10L);
        when(produitRepository.countByActive(true)).thenReturn(10L);
        when(stockRepository.findStocksSousMinimum()).thenReturn(Arrays.asList());
        when(bonCommandeRepository.count()).thenReturn(0L);
        when(bonAchatRepository.count()).thenReturn(0L);

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertEquals(caMois, result.getChiffreAffairesMois());
        verify(factureRepository).sumMontantByDateBetween(eq(firstDayOfMonth), eq(today));
    }

    @Test
    void getTableauDeBord_shouldCalculateCAForCurrentYear() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = today.withDayOfYear(1);
        BigDecimal caAnnee = new BigDecimal("150000.00");

        when(factureRepository.sumMontantByDateBetween(eq(firstDayOfYear), eq(today)))
                .thenReturn(caAnnee);
        when(factureRepository.sumMontantByDateBetween(any(LocalDate.class), eq(today)))
                .thenReturn(caAnnee);
        when(factureRepository.count()).thenReturn(50L);
        when(factureRepository.countByStatut(anyString())).thenReturn(0L);
        when(clientRepository.count()).thenReturn(30L);
        when(produitRepository.count()).thenReturn(100L);
        when(produitRepository.countByActive(true)).thenReturn(90L);
        when(stockRepository.findStocksSousMinimum()).thenReturn(Arrays.asList());
        when(bonCommandeRepository.count()).thenReturn(0L);
        when(bonAchatRepository.count()).thenReturn(0L);

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertEquals(caAnnee, result.getChiffreAffairesAnnee());
        verify(factureRepository).sumMontantByDateBetween(eq(firstDayOfYear), eq(today));
    }

    @Test
    void getTableauDeBord_shouldCountStocksWithAlerts() {
        // Given
        List<Stock> stocksSousMinimum = Arrays.asList(
                Stock.builder().build(),
                Stock.builder().build(),
                Stock.builder().build(),
                Stock.builder().build(),
                Stock.builder().build()
        );

        when(factureRepository.sumMontantByDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(factureRepository.count()).thenReturn(0L);
        when(factureRepository.countByStatut(anyString())).thenReturn(0L);
        when(clientRepository.count()).thenReturn(0L);
        when(produitRepository.count()).thenReturn(0L);
        when(produitRepository.countByActive(true)).thenReturn(0L);
        when(stockRepository.findStocksSousMinimum()).thenReturn(stocksSousMinimum);
        when(bonCommandeRepository.count()).thenReturn(0L);
        when(bonAchatRepository.count()).thenReturn(0L);

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertEquals(5L, result.getNombreProduitsAlerteStock());
        verify(stockRepository).findStocksSousMinimum();
    }

    @Test
    void getTableauDeBord_shouldHaveEmptyLists() {
        // Given - Setup minimal mocking
        when(factureRepository.count()).thenReturn(0L);
        when(factureRepository.countByStatut(anyString())).thenReturn(0L);
        when(clientRepository.count()).thenReturn(0L);
        when(produitRepository.count()).thenReturn(0L);
        when(produitRepository.countByActive(true)).thenReturn(0L);
        when(stockRepository.findStocksSousMinimum()).thenReturn(Arrays.asList());
        when(bonCommandeRepository.count()).thenReturn(0L);
        when(bonAchatRepository.count()).thenReturn(0L);

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertNotNull(result.getTopProduits());
        assertTrue(result.getTopProduits().isEmpty());
        assertNotNull(result.getTopClients());
        assertTrue(result.getTopClients().isEmpty());
        assertNotNull(result.getEvolutionCA12Mois());
        assertTrue(result.getEvolutionCA12Mois().isEmpty());
    }

    @Test
    void getTableauDeBord_shouldSetCorrectDateGeneration() {
        // Given
        when(factureRepository.count()).thenReturn(0L);
        when(factureRepository.countByStatut(anyString())).thenReturn(0L);
        when(clientRepository.count()).thenReturn(0L);
        when(produitRepository.count()).thenReturn(0L);
        when(produitRepository.countByActive(true)).thenReturn(0L);
        when(stockRepository.findStocksSousMinimum()).thenReturn(Arrays.asList());
        when(bonCommandeRepository.count()).thenReturn(0L);
        when(bonAchatRepository.count()).thenReturn(0L);

        LocalDate expectedDate = LocalDate.now();

        // When
        TableauDeBordResponse result = tableauDeBordService.getTableauDeBord();

        // Then
        assertEquals(expectedDate, result.getDateGeneration());
    }
}
