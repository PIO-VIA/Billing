package com.example.account.controller;

import com.example.account.dto.request.PaiementCreateRequest;
import com.example.account.dto.request.PaiementUpdateRequest;
import com.example.account.dto.response.PaiementResponse;
import com.example.account.model.enums.TypePaiement;
import com.example.account.service.PaiementService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaiementController.class)
class PaiementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaiementService paiementService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaiementResponse paiementResponse;
    private PaiementCreateRequest paiementCreateRequest;
    private PaiementUpdateRequest paiementUpdateRequest;
    private UUID paiementId;
    private UUID clientId;
    private UUID factureId;

    @BeforeEach
    void setUp() {
        paiementId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        factureId = UUID.randomUUID();

        paiementResponse = new PaiementResponse();
        paiementResponse.setIdPaiement(paiementId);
        paiementResponse.setIdClient(clientId);
        paiementResponse.setIdFacture(factureId);
        paiementResponse.setMontant(new BigDecimal("150.00"));

        paiementCreateRequest = new PaiementCreateRequest();
        paiementCreateRequest.setIdClient(clientId);
        paiementCreateRequest.setIdFacture(factureId);
        paiementCreateRequest.setMontant(new BigDecimal("150.00"));
        paiementCreateRequest.setDate(LocalDate.now());
        paiementCreateRequest.setJournal("BANQUE");
        paiementCreateRequest.setModePaiement(TypePaiement.CARTE_BANCAIRE);

        paiementUpdateRequest = new PaiementUpdateRequest();
        paiementUpdateRequest.setMontant(new BigDecimal("200.00"));
    }

    @Test
    void createPaiement_shouldReturnCreatedPaiement() throws Exception {
        when(paiementService.createPaiement(any(PaiementCreateRequest.class))).thenReturn(paiementResponse);

        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiementCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPaiement").value(paiementId.toString()));
    }

    @Test
    void updatePaiement_shouldReturnUpdatedPaiement() throws Exception {
        when(paiementService.updatePaiement(eq(paiementId), any(PaiementUpdateRequest.class))).thenReturn(paiementResponse);

        mockMvc.perform(put("/api/paiements/{paiementId}", paiementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiementUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaiement").value(paiementId.toString()));
    }

    @Test
    void getPaiementById_shouldReturnPaiement() throws Exception {
        when(paiementService.getPaiementById(paiementId)).thenReturn(paiementResponse);

        mockMvc.perform(get("/api/paiements/{paiementId}", paiementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaiement").value(paiementId.toString()));
    }

    @Test
    void getAllPaiements_shouldReturnPaiementList() throws Exception {
        List<PaiementResponse> paiements = Collections.singletonList(paiementResponse);
        when(paiementService.getAllPaiements()).thenReturn(paiements);

        mockMvc.perform(get("/api/paiements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPaiement").value(paiementId.toString()));
    }

    @Test
    void getAllPaiementsPaginated_shouldReturnPaiementPage() throws Exception {
        Page<PaiementResponse> paiementPage = new PageImpl<>(Collections.singletonList(paiementResponse));
        when(paiementService.getAllPaiements(any(Pageable.class))).thenReturn(paiementPage);

        mockMvc.perform(get("/api/paiements/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idPaiement").value(paiementId.toString()));
    }

    @Test
    void getPaiementsByClient_shouldReturnPaiementList() throws Exception {
        List<PaiementResponse> paiements = Collections.singletonList(paiementResponse);
        when(paiementService.getPaiementsByClient(clientId)).thenReturn(paiements);

        mockMvc.perform(get("/api/paiements/client/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClient").value(clientId.toString()));
    }

    @Test
    void getPaiementsByFacture_shouldReturnPaiementList() throws Exception {
        List<PaiementResponse> paiements = Collections.singletonList(paiementResponse);
        when(paiementService.getPaiementsByFacture(factureId)).thenReturn(paiements);

        mockMvc.perform(get("/api/paiements/facture/{factureId}", factureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFacture").value(factureId.toString()));
    }

    @Test
    void getPaiementsByModePaiement_shouldReturnPaiementList() throws Exception {
        List<PaiementResponse> paiements = Collections.singletonList(paiementResponse);
        when(paiementService.getPaiementsByModePaiement(TypePaiement.CARTE_BANCAIRE)).thenReturn(paiements);

        mockMvc.perform(get("/api/paiements/mode/{modePaiement}", TypePaiement.CARTE_BANCAIRE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPaiement").value(paiementId.toString()));
    }

    @Test
    void getPaiementsByPeriode_shouldReturnPaiementList() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<PaiementResponse> paiements = Collections.singletonList(paiementResponse);
        when(paiementService.getPaiementsByPeriode(startDate, endDate)).thenReturn(paiements);

        mockMvc.perform(get("/api/paiements/periode")
                        .param("dateDebut", startDate.toString())
                        .param("dateFin", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPaiement").value(paiementId.toString()));
    }

    @Test
    void deletePaiement_shouldReturnNoContent() throws Exception {
        doNothing().when(paiementService).deletePaiement(paiementId);

        mockMvc.perform(delete("/api/paiements/{paiementId}", paiementId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTotalPaiementsByClient_shouldReturnTotal() throws Exception {
        BigDecimal total = new BigDecimal("500.00");
        when(paiementService.getTotalPaiementsByClient(clientId)).thenReturn(total);

        mockMvc.perform(get("/api/paiements/total/client/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(content().string(total.toString()));
    }

    @Test
    void getTotalPaiementsByFacture_shouldReturnTotal() throws Exception {
        BigDecimal total = new BigDecimal("150.00");
        when(paiementService.getTotalPaiementsByFacture(factureId)).thenReturn(total);

        mockMvc.perform(get("/api/paiements/total/facture/{factureId}", factureId))
                .andExpect(status().isOk())
                .andExpect(content().string(total.toString()));
    }



    @Test
    void getTotalPaiementsByPeriode_shouldReturnTotal() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        BigDecimal total = new BigDecimal("1234.56");
        when(paiementService.getTotalPaiementsByPeriode(startDate, endDate)).thenReturn(total);

        mockMvc.perform(get("/api/paiements/total/periode")
                        .param("dateDebut", startDate.toString())
                        .param("dateFin", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(total.toString()));
    }
}
