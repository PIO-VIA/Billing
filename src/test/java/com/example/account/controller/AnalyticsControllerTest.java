package com.example.account.controller;

import com.example.account.model.entity.Stock;
import com.example.account.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FactureRepository factureRepository;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private ProduitRepository produitRepository;

    @MockBean
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        // Setup common mock behavior
    }

    @Test
    void getRapportVentes_shouldReturnSalesReport() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 10, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 31);
        BigDecimal montantTotal = new BigDecimal("50000.00");
        Long nombreFactures = 25L;

        when(factureRepository.sumMontantByDateBetween(startDate, endDate)).thenReturn(montantTotal);
        when(factureRepository.countByDateBetween(startDate, endDate)).thenReturn(nombreFactures);

        // When & Then
        mockMvc.perform(get("/api/analytics/ventes/periode")
                        .param("startDate", "2025-10-01")
                        .param("endDate", "2025-10-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.periode.debut").value("2025-10-01"))
                .andExpect(jsonPath("$.periode.fin").value("2025-10-31"))
                .andExpect(jsonPath("$.montantTotal").value(50000.00))
                .andExpect(jsonPath("$.nombreFactures").value(25))
                .andExpect(jsonPath("$.montantMoyen").value(2000.00));

        verify(factureRepository).sumMontantByDateBetween(startDate, endDate);
        verify(factureRepository).countByDateBetween(startDate, endDate);
    }

    @Test
    void getRapportVentes_shouldHandleNullValues() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 10, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 31);

        when(factureRepository.sumMontantByDateBetween(startDate, endDate)).thenReturn(null);
        when(factureRepository.countByDateBetween(startDate, endDate)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/analytics/ventes/periode")
                        .param("startDate", "2025-10-01")
                        .param("endDate", "2025-10-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantTotal").value(0))
                .andExpect(jsonPath("$.nombreFactures").value(0))
                .andExpect(jsonPath("$.montantMoyen").value(0));

        verify(factureRepository).sumMontantByDateBetween(startDate, endDate);
        verify(factureRepository).countByDateBetween(startDate, endDate);
    }

    @Test
    void getRapportVentes_shouldCalculateAverageCorrectly() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 10, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 31);
        BigDecimal montantTotal = new BigDecimal("12500.75");
        Long nombreFactures = 5L;

        when(factureRepository.sumMontantByDateBetween(startDate, endDate)).thenReturn(montantTotal);
        when(factureRepository.countByDateBetween(startDate, endDate)).thenReturn(nombreFactures);

        // When & Then
        mockMvc.perform(get("/api/analytics/ventes/periode")
                        .param("startDate", "2025-10-01")
                        .param("endDate", "2025-10-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantMoyen").value(2500.15));
    }

    @Test
    void getStocksAlertes_shouldReturnStockAlerts() throws Exception {
        // Given
        Stock stock1 = Stock.builder()
                .idStock(UUID.randomUUID())
                .nomProduit("Produit 1")
                .quantite(new BigDecimal("5"))
                .stockMinimum(new BigDecimal("10"))
                .build();

        Stock stock2 = Stock.builder()
                .idStock(UUID.randomUUID())
                .nomProduit("Produit 2")
                .quantite(new BigDecimal("3"))
                .stockMinimum(new BigDecimal("15"))
                .build();

        Stock stockCritique = Stock.builder()
                .idStock(UUID.randomUUID())
                .nomProduit("Produit Critique")
                .quantite(BigDecimal.ZERO)
                .build();

        List<Stock> stocksSousMinimum = Arrays.asList(stock1, stock2);
        List<Stock> stocksCritiques = Arrays.asList(stockCritique);

        when(stockRepository.findStocksSousMinimum()).thenReturn(stocksSousMinimum);
        when(stockRepository.findStocksAvecSeuilCritique(BigDecimal.ZERO)).thenReturn(stocksCritiques);

        // When & Then
        mockMvc.perform(get("/api/analytics/stocks/alertes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stocksSousMinimum").isArray())
                .andExpect(jsonPath("$.stocksSousMinimum.length()").value(2))
                .andExpect(jsonPath("$.stocksCritiques").isArray())
                .andExpect(jsonPath("$.stocksCritiques.length()").value(1))
                .andExpect(jsonPath("$.nombreAlertes").value(2));

        verify(stockRepository).findStocksSousMinimum();
        verify(stockRepository).findStocksAvecSeuilCritique(BigDecimal.ZERO);
    }

    @Test
    void getStocksAlertes_shouldHandleEmptyStocks() throws Exception {
        // Given
        when(stockRepository.findStocksSousMinimum()).thenReturn(Arrays.asList());
        when(stockRepository.findStocksAvecSeuilCritique(BigDecimal.ZERO)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/analytics/stocks/alertes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocksSousMinimum").isEmpty())
                .andExpect(jsonPath("$.stocksCritiques").isEmpty())
                .andExpect(jsonPath("$.nombreAlertes").value(0));
    }

    @Test
    void getTopClients_shouldReturnEmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/clients/top")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getTopClients_shouldUseDefaultLimit() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/clients/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTopClients_shouldAcceptCustomLimit() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/clients/top")
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTopProduits_shouldReturnEmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/produits/top")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getTopProduits_shouldUseDefaultLimit() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/produits/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTopProduits_shouldAcceptCustomLimit() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/produits/top")
                        .param("limit", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getValeurStock_shouldReturnStockValue() throws Exception {
        // Given
        BigDecimal valeurTotale = new BigDecimal("125000.50");
        when(stockRepository.calculerValeurTotaleStock()).thenReturn(valeurTotale);

        // When & Then
        mockMvc.perform(get("/api/analytics/stocks/valeur")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.valeurTotale").value(125000.50))
                .andExpect(jsonPath("$.devise").value("EUR"))
                .andExpect(jsonPath("$.dateCalcul").value(LocalDate.now().toString()));

        verify(stockRepository).calculerValeurTotaleStock();
    }

    @Test
    void getValeurStock_shouldHandleNullValue() throws Exception {
        // Given
        when(stockRepository.calculerValeurTotaleStock()).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/analytics/stocks/valeur")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valeurTotale").value(0))
                .andExpect(jsonPath("$.devise").value("EUR"))
                .andExpect(jsonPath("$.dateCalcul").exists());

        verify(stockRepository).calculerValeurTotaleStock();
    }

    @Test
    void getRapportVentes_shouldRequireStartDateParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/ventes/periode")
                        .param("endDate", "2025-10-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRapportVentes_shouldRequireEndDateParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/analytics/ventes/periode")
                        .param("startDate", "2025-10-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
