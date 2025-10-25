package com.example.account.controller;

import com.example.account.dto.request.RemboursementCreateRequest;
import com.example.account.dto.request.RemboursementUpdateRequest;
import com.example.account.dto.response.RemboursementResponse;
import com.example.account.service.RemboursementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RemboursementController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class RemboursementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RemboursementService remboursementService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void createRemboursement_shouldReturnCreatedRemboursement() throws Exception {
        when(remboursementService.createRemboursement(any(RemboursementCreateRequest.class))).thenReturn(remboursementResponse);

        mockMvc.perform(post("/api/remboursements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(remboursementCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRemboursement").value(remboursementId.toString()))
                .andExpect(jsonPath("$.montant").value(1000.00))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    void updateRemboursement_shouldReturnUpdatedRemboursement() throws Exception {
        when(remboursementService.updateRemboursement(eq(remboursementId), any(RemboursementUpdateRequest.class))).thenReturn(remboursementResponse);

        mockMvc.perform(put("/api/remboursements/{remboursementId}", remboursementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(remboursementUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRemboursement").value(remboursementId.toString()));
    }

    @Test
    void getRemboursementById_shouldReturnRemboursement() throws Exception {
        when(remboursementService.getRemboursementById(remboursementId)).thenReturn(remboursementResponse);

        mockMvc.perform(get("/api/remboursements/{remboursementId}", remboursementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRemboursement").value(remboursementId.toString()))
                .andExpect(jsonPath("$.montant").value(1000.00));
    }

    @Test
    void getAllRemboursements_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getAllRemboursements()).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRemboursement").value(remboursementId.toString()));
    }

    @Test
    void getAllRemboursementsPaginated_shouldReturnPaginatedRemboursementList() throws Exception {
        Page<RemboursementResponse> remboursements = new PageImpl<>(Collections.singletonList(remboursementResponse));
        when(remboursementService.getAllRemboursements(any(Pageable.class))).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idRemboursement").value(remboursementId.toString()));
    }

    @Test
    void getRemboursementsByClient_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getRemboursementsByClient(clientId)).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/client/{idClient}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClient").value(clientId.toString()));
    }

    @Test
    void getRemboursementsByFacture_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getRemboursementsByFacture(factureId)).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/facture/{idFacture}", factureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFacture").value(factureId.toString()));
    }

    @Test
    void getRemboursementsByStatut_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getRemboursementsByStatut("EN_ATTENTE")).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/statut/{statut}", "EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"));
    }

    @Test
    void getRemboursementsByDevise_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getRemboursementsByDevise("EUR")).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/devise/{devise}", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].devise").value("EUR"));
    }

    @Test
    void getRemboursementsByDateFacturation_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        when(remboursementService.getRemboursementsByDateFacturationBetween(startDate, endDate)).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/date-facturation")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRemboursement").value(remboursementId.toString()));
    }

    @Test
    void getRemboursementsByMontantRange_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getRemboursementsByMontantBetween(new BigDecimal("500.00"), new BigDecimal("1500.00")))
                .thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/montant-range")
                        .param("minAmount", "500.00")
                        .param("maxAmount", "1500.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRemboursement").value(remboursementId.toString()));
    }

    @Test
    void getOverdueRemboursements_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getOverdueRemboursements()).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/retard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRemboursement").value(remboursementId.toString()));
    }

    @Test
    void getRemboursementsByClientAndStatut_shouldReturnRemboursementList() throws Exception {
        List<RemboursementResponse> remboursements = Collections.singletonList(remboursementResponse);
        when(remboursementService.getRemboursementsByClientAndStatut(clientId, "EN_ATTENTE")).thenReturn(remboursements);

        mockMvc.perform(get("/api/remboursements/client/{idClient}/statut/{statut}", clientId, "EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClient").value(clientId.toString()))
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"));
    }

    @Test
    void deleteRemboursement_shouldReturnNoContent() throws Exception {
        doNothing().when(remboursementService).deleteRemboursement(remboursementId);

        mockMvc.perform(delete("/api/remboursements/{remboursementId}", remboursementId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTotalMontantByClient_shouldReturnTotal() throws Exception {
        when(remboursementService.getTotalMontantByClient(clientId)).thenReturn(new BigDecimal("5000.00"));

        mockMvc.perform(get("/api/remboursements/total/client/{idClient}", clientId))
                .andExpect(status().isOk())
                .andExpect(content().string("5000.00"));
    }

    @Test
    void getTotalMontantByStatut_shouldReturnTotal() throws Exception {
        when(remboursementService.getTotalMontantByStatut("EN_ATTENTE")).thenReturn(new BigDecimal("10000.00"));

        mockMvc.perform(get("/api/remboursements/total/statut/{statut}", "EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(content().string("10000.00"));
    }

    @Test
    void countByStatut_shouldReturnCount() throws Exception {
        when(remboursementService.countByStatut("EN_ATTENTE")).thenReturn(15L);

        mockMvc.perform(get("/api/remboursements/count/statut/{statut}", "EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void countByClient_shouldReturnCount() throws Exception {
        when(remboursementService.countByClient(clientId)).thenReturn(8L);

        mockMvc.perform(get("/api/remboursements/count/client/{idClient}", clientId))
                .andExpect(status().isOk())
                .andExpect(content().string("8"));
    }

    @Test
    void updateStatut_shouldReturnUpdatedRemboursement() throws Exception {
        when(remboursementService.updateStatut(remboursementId, "VALIDE")).thenReturn(remboursementResponse);

        mockMvc.perform(put("/api/remboursements/{remboursementId}/statut", remboursementId)
                        .param("statut", "VALIDE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRemboursement").value(remboursementId.toString()));
    }
}
