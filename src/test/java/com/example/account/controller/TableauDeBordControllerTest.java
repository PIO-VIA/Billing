package com.example.account.controller;

import com.example.account.dto.response.TableauDeBordResponse;
import com.example.account.service.TableauDeBordService;
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
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableauDeBordController.class)
class TableauDeBordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TableauDeBordService tableauDeBordService;

    private TableauDeBordResponse dashboardResponse;

    @BeforeEach
    void setUp() {
        dashboardResponse = TableauDeBordResponse.builder()
                .chiffreAffairesMois(new BigDecimal("50000.00"))
                .chiffreAffairesAnnee(new BigDecimal("500000.00"))
                .evolutionCA(new BigDecimal("10.5"))
                .nombreFacturesEmises(100L)
                .nombreFacturesPayees(60L)
                .nombreFacturesEnAttente(30L)
                .montantFacturesEnAttente(new BigDecimal("15000.00"))
                .montantFacturesEnRetard(new BigDecimal("5000.00"))
                .nombreClients(50L)
                .nombreNouveauxClients(5L)
                .montantMoyenParClient(new BigDecimal("10000.00"))
                .nombreProduits(200L)
                .nombreProduitsActifs(180L)
                .nombreProduitsAlerteStock(10L)
                .valeurTotaleStock(new BigDecimal("75000.00"))
                .nombreMouvementsStock(150L)
                .montantAchatsMois(new BigDecimal("30000.00"))
                .nombreBonsCommande(25L)
                .nombreBonsAchat(20L)
                .soldeTresorerie(new BigDecimal("100000.00"))
                .encaissementsPrevus(new BigDecimal("40000.00"))
                .decaissementsPrevus(new BigDecimal("35000.00"))
                .topProduits(new ArrayList<>())
                .topClients(new ArrayList<>())
                .evolutionCA12Mois(new ArrayList<>())
                .dateGeneration(LocalDate.now())
                .build();
    }

    @Test
    void getTableauDeBord_shouldReturnDashboard() throws Exception {
        // Given
        when(tableauDeBordService.getTableauDeBord()).thenReturn(dashboardResponse);

        // When & Then
        mockMvc.perform(get("/api/tableau-de-bord")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chiffreAffairesMois").value(50000.00))
                .andExpect(jsonPath("$.chiffreAffairesAnnee").value(500000.00))
                .andExpect(jsonPath("$.evolutionCA").value(10.5))
                .andExpect(jsonPath("$.nombreFacturesEmises").value(100))
                .andExpect(jsonPath("$.nombreFacturesPayees").value(60))
                .andExpect(jsonPath("$.nombreFacturesEnAttente").value(30))
                .andExpect(jsonPath("$.montantFacturesEnAttente").value(15000.00))
                .andExpect(jsonPath("$.nombreClients").value(50))
                .andExpect(jsonPath("$.nombreNouveauxClients").value(5))
                .andExpect(jsonPath("$.nombreProduits").value(200))
                .andExpect(jsonPath("$.nombreProduitsActifs").value(180))
                .andExpect(jsonPath("$.nombreProduitsAlerteStock").value(10))
                .andExpect(jsonPath("$.valeurTotaleStock").value(75000.00))
                .andExpect(jsonPath("$.nombreBonsCommande").value(25))
                .andExpect(jsonPath("$.nombreBonsAchat").value(20))
                .andExpect(jsonPath("$.soldeTresorerie").value(100000.00))
                .andExpect(jsonPath("$.topProduits").isArray())
                .andExpect(jsonPath("$.topClients").isArray())
                .andExpect(jsonPath("$.evolutionCA12Mois").isArray())
                .andExpect(jsonPath("$.dateGeneration").exists());

        verify(tableauDeBordService).getTableauDeBord();
    }

    @Test
    void getTableauDeBord_shouldReturnEmptyListsWhenNoData() throws Exception {
        // Given
        TableauDeBordResponse emptyResponse = TableauDeBordResponse.builder()
                .chiffreAffairesMois(BigDecimal.ZERO)
                .chiffreAffairesAnnee(BigDecimal.ZERO)
                .evolutionCA(BigDecimal.ZERO)
                .nombreFacturesEmises(0L)
                .nombreFacturesPayees(0L)
                .nombreFacturesEnAttente(0L)
                .montantFacturesEnAttente(BigDecimal.ZERO)
                .montantFacturesEnRetard(BigDecimal.ZERO)
                .nombreClients(0L)
                .nombreNouveauxClients(0L)
                .montantMoyenParClient(BigDecimal.ZERO)
                .nombreProduits(0L)
                .nombreProduitsActifs(0L)
                .nombreProduitsAlerteStock(0L)
                .valeurTotaleStock(BigDecimal.ZERO)
                .nombreMouvementsStock(0L)
                .montantAchatsMois(BigDecimal.ZERO)
                .nombreBonsCommande(0L)
                .nombreBonsAchat(0L)
                .soldeTresorerie(BigDecimal.ZERO)
                .encaissementsPrevus(BigDecimal.ZERO)
                .decaissementsPrevus(BigDecimal.ZERO)
                .topProduits(new ArrayList<>())
                .topClients(new ArrayList<>())
                .evolutionCA12Mois(new ArrayList<>())
                .dateGeneration(LocalDate.now())
                .build();

        when(tableauDeBordService.getTableauDeBord()).thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/tableau-de-bord")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chiffreAffairesMois").value(0))
                .andExpect(jsonPath("$.nombreFacturesEmises").value(0))
                .andExpect(jsonPath("$.nombreClients").value(0))
                .andExpect(jsonPath("$.topProduits").isEmpty())
                .andExpect(jsonPath("$.topClients").isEmpty())
                .andExpect(jsonPath("$.evolutionCA12Mois").isEmpty());

        verify(tableauDeBordService).getTableauDeBord();
    }

    @Test
    void getTableauDeBord_shouldCallServiceOnce() throws Exception {
        // Given
        when(tableauDeBordService.getTableauDeBord()).thenReturn(dashboardResponse);

        // When
        mockMvc.perform(get("/api/tableau-de-bord"));

        // Then
        verify(tableauDeBordService, times(1)).getTableauDeBord();
    }
}
