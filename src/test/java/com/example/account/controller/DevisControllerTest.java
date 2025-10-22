package com.example.account.controller;

import com.example.account.dto.request.DevisCreateRequest;
import com.example.account.dto.response.DevisResponse;
import com.example.account.model.enums.StatutDevis;
import com.example.account.service.DevisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DevisController.class)
class DevisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DevisService devisService;

    @Autowired
    private ObjectMapper objectMapper;

    private DevisResponse devisResponse;
    private DevisCreateRequest devisCreateRequest;
    private UUID devisId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        devisId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        devisResponse = new DevisResponse();
        devisResponse.setIdDevis(devisId);
        devisResponse.setNumeroDevis("DEV-2025-001");
        devisResponse.setIdClient(clientId);

        devisCreateRequest = new DevisCreateRequest();
        devisCreateRequest.setDateCreation(LocalDate.now());
        devisCreateRequest.setDateValidite(LocalDate.now().plusDays(30));
        devisCreateRequest.setStatut(StatutDevis.BROUILLON);
        devisCreateRequest.setIdClient(clientId);
    }

    @Test
    void createDevis_shouldReturnCreatedDevis() throws Exception {
        when(devisService.createDevis(any(DevisCreateRequest.class))).thenReturn(devisResponse);

        mockMvc.perform(post("/api/devis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devisCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDevis").value(devisId.toString()))
                .andExpect(jsonPath("$.numeroDevis").value("DEV-2025-001"));
    }

    @Test
    void updateDevis_shouldReturnUpdatedDevis() throws Exception {
        when(devisService.updateDevis(eq(devisId), any(DevisCreateRequest.class))).thenReturn(devisResponse);

        mockMvc.perform(put("/api/devis/{devisId}", devisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devisCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDevis").value(devisId.toString()));
    }

    @Test
    void getDevisById_shouldReturnDevis() throws Exception {
        when(devisService.getDevisById(devisId)).thenReturn(devisResponse);

        mockMvc.perform(get("/api/devis/{devisId}", devisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDevis").value(devisId.toString()));
    }

    @Test
    void getDevisByNumero_shouldReturnDevis() throws Exception {
        when(devisService.getDevisByNumero("DEV-2025-001")).thenReturn(devisResponse);

        mockMvc.perform(get("/api/devis/numero/{numeroDevis}", "DEV-2025-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroDevis").value("DEV-2025-001"));
    }

    @Test
    void getAllDevis_shouldReturnDevisList() throws Exception {
        List<DevisResponse> devisList = Collections.singletonList(devisResponse);
        when(devisService.getAllDevis()).thenReturn(devisList);

        mockMvc.perform(get("/api/devis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDevis").value(devisId.toString()));
    }

    @Test
    void getAllDevisPaginated_shouldReturnDevisPage() throws Exception {
        Page<DevisResponse> devisPage = new PageImpl<>(Collections.singletonList(devisResponse));
        when(devisService.getAllDevis(any(Pageable.class))).thenReturn(devisPage);

        mockMvc.perform(get("/api/devis/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idDevis").value(devisId.toString()));
    }

    @Test
    void getDevisByClient_shouldReturnDevisList() throws Exception {
        List<DevisResponse> devisList = Collections.singletonList(devisResponse);
        when(devisService.getDevisByClient(clientId)).thenReturn(devisList);

        mockMvc.perform(get("/api/devis/client/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClient").value(clientId.toString()));
    }

    @Test
    void getDevisByStatut_shouldReturnDevisList() throws Exception {
        List<DevisResponse> devisList = Collections.singletonList(devisResponse);
        when(devisService.getDevisByStatut(StatutDevis.ENVOYE)).thenReturn(devisList);

        mockMvc.perform(get("/api/devis/statut/{statut}", StatutDevis.ENVOYE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDevis").value(devisId.toString()));
    }

    @Test
    void getDevisExpires_shouldReturnExpiredDevisList() throws Exception {
        List<DevisResponse> devisList = Collections.singletonList(devisResponse);
        when(devisService.getDevisExpires()).thenReturn(devisList);

        mockMvc.perform(get("/api/devis/expires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDevis").value(devisId.toString()));
    }

    @Test
    void getDevisByPeriode_shouldReturnDevisList() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<DevisResponse> devisList = Collections.singletonList(devisResponse);
        when(devisService.getDevisByPeriode(startDate, endDate)).thenReturn(devisList);

        mockMvc.perform(get("/api/devis/periode")
                        .param("dateDebut", startDate.toString())
                        .param("dateFin", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDevis").value(devisId.toString()));
    }

    @Test
    void deleteDevis_shouldReturnNoContent() throws Exception {
        doNothing().when(devisService).deleteDevis(devisId);

        mockMvc.perform(delete("/api/devis/{devisId}", devisId))
                .andExpect(status().isNoContent());
    }

    @Test
    void accepterDevis_shouldReturnUpdatedDevis() throws Exception {
        when(devisService.accepterDevis(devisId)).thenReturn(devisResponse);

        mockMvc.perform(put("/api/devis/{devisId}/accepter", devisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDevis").value(devisId.toString()));
    }

    @Test
    void refuserDevis_shouldReturnUpdatedDevis() throws Exception {
        String motif = "Trop cher";
        when(devisService.refuserDevis(devisId, motif)).thenReturn(devisResponse);

        mockMvc.perform(put("/api/devis/{devisId}/refuser", devisId)
                        .param("motifRefus", motif))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDevis").value(devisId.toString()));
    }
}
