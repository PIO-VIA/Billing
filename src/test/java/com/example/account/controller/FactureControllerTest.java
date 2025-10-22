package com.example.account.controller;

import com.example.account.dto.request.FactureCreateRequest;
import com.example.account.dto.request.FactureUpdateRequest;
import com.example.account.dto.response.FactureResponse;
import com.example.account.model.enums.StatutFacture;
import com.example.account.service.FactureService;
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

@WebMvcTest(FactureController.class)
class FactureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FactureService factureService;

    @Autowired
    private ObjectMapper objectMapper;

    private FactureResponse factureResponse;
    private FactureCreateRequest factureCreateRequest;
    private FactureUpdateRequest factureUpdateRequest;
    private UUID factureId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        factureId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        factureResponse = new FactureResponse();
        factureResponse.setIdFacture(factureId);
        factureResponse.setNumeroFacture("FAC-2025-001");
        factureResponse.setIdClient(clientId);

        factureCreateRequest = new FactureCreateRequest();
        factureCreateRequest.setIdClient(clientId);

        factureUpdateRequest = new FactureUpdateRequest();
    }

    @Test
    void createFacture_shouldReturnCreatedFacture() throws Exception {
        when(factureService.createFacture(any(FactureCreateRequest.class))).thenReturn(factureResponse);

        mockMvc.perform(post("/api/factures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(factureCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(factureId.toString()));
    }

    @Test
    void updateFacture_shouldReturnUpdatedFacture() throws Exception {
        when(factureService.updateFacture(eq(factureId), any(FactureUpdateRequest.class))).thenReturn(factureResponse);

        mockMvc.perform(put("/api/factures/{factureId}", factureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(factureUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(factureId.toString()));
    }

    @Test
    void getFactureById_shouldReturnFacture() throws Exception {
        when(factureService.getFactureById(factureId)).thenReturn(factureResponse);

        mockMvc.perform(get("/api/factures/{factureId}", factureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(factureId.toString()));
    }

    @Test
    void getFactureByNumero_shouldReturnFacture() throws Exception {
        when(factureService.getFactureByNumero("FAC-2025-001")).thenReturn(factureResponse);

        mockMvc.perform(get("/api/factures/numero/{numeroFacture}", "FAC-2025-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroFacture").value("FAC-2025-001"));
    }

    @Test
    void getAllFactures_shouldReturnFactureList() throws Exception {
        List<FactureResponse> factures = Collections.singletonList(factureResponse);
        when(factureService.getAllFactures()).thenReturn(factures);

        mockMvc.perform(get("/api/factures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factureId.toString()));
    }

    @Test
    void getAllFacturesPaginated_shouldReturnFacturePage() throws Exception {
        Page<FactureResponse> facturePage = new PageImpl<>(Collections.singletonList(factureResponse));
        when(factureService.getAllFactures(any(Pageable.class))).thenReturn(facturePage);

        mockMvc.perform(get("/api/factures/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(factureId.toString()));
    }

    @Test
    void getFacturesByClient_shouldReturnFactureList() throws Exception {
        List<FactureResponse> factures = Collections.singletonList(factureResponse);
        when(factureService.getFacturesByClient(clientId)).thenReturn(factures);

        mockMvc.perform(get("/api/factures/client/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClient").value(clientId.toString()));
    }

    @Test
    void getFacturesByEtat_shouldReturnFactureList() throws Exception {
        List<FactureResponse> factures = Collections.singletonList(factureResponse);
        when(factureService.getFacturesByEtat(StatutFacture.BROUILLON)).thenReturn(factures);

        mockMvc.perform(get("/api/factures/etat/{etat}", StatutFacture.BROUILLON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factureId.toString()));
    }

    @Test
    void getFacturesEnRetard_shouldReturnFactureList() throws Exception {
        List<FactureResponse> factures = Collections.singletonList(factureResponse);
        when(factureService.getFacturesEnRetard()).thenReturn(factures);

        mockMvc.perform(get("/api/factures/retard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factureId.toString()));
    }

    @Test
    void getFacturesNonPayees_shouldReturnFactureList() throws Exception {
        List<FactureResponse> factures = Collections.singletonList(factureResponse);
        when(factureService.getFacturesNonPayees()).thenReturn(factures);

        mockMvc.perform(get("/api/factures/non-payees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factureId.toString()));
    }

    @Test
    void getFacturesByPeriode_shouldReturnFactureList() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<FactureResponse> factures = Collections.singletonList(factureResponse);
        when(factureService.getFacturesByPeriode(startDate, endDate)).thenReturn(factures);

        mockMvc.perform(get("/api/factures/periode")
                        .param("dateDebut", startDate.toString())
                        .param("dateFin", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(factureId.toString()));
    }

    @Test
    void deleteFacture_shouldReturnNoContent() throws Exception {
        doNothing().when(factureService).deleteFacture(factureId);

        mockMvc.perform(delete("/api/factures/{factureId}", factureId))
                .andExpect(status().isNoContent());
    }

    @Test
    void marquerCommePaye_shouldReturnUpdatedFacture() throws Exception {
        when(factureService.marquerCommePaye(factureId)).thenReturn(factureResponse);

        mockMvc.perform(put("/api/factures/{factureId}/marquer-paye", factureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(factureId.toString()));
    }

    @Test
    void enregistrerPaiement_shouldReturnUpdatedFacture() throws Exception {
        BigDecimal montant = new BigDecimal("100.00");
        when(factureService.enregistrerPaiement(factureId, montant)).thenReturn(factureResponse);

        mockMvc.perform(put("/api/factures/{factureId}/paiement", factureId)
                        .param("montantPaye", montant.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(factureId.toString()));
    }

    @Test
    void countByEtat_shouldReturnCount() throws Exception {
        when(factureService.countByEtat(StatutFacture.PAYE)).thenReturn(10L);

        mockMvc.perform(get("/api/factures/count/etat/{etat}", StatutFacture.PAYE))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void downloadFacturePdf_shouldReturnPdfFile() throws Exception {
        byte[] pdfBytes = "PDF Content".getBytes();
        when(factureService.genererPdfFacture(factureId)).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/factures/{factureId}/pdf", factureId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=facture_" + factureId + ".pdf"))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void envoyerFactureParEmail_shouldReturnOk() throws Exception {
        doNothing().when(factureService).envoyerFactureParEmail(factureId);

        mockMvc.perform(post("/api/factures/{factureId}/envoyer-email", factureId))
                .andExpect(status().isOk());
    }

    @Test
    void envoyerRappelPaiement_shouldReturnOk() throws Exception {
        doNothing().when(factureService).envoyerRappelPaiement(factureId);

        mockMvc.perform(post("/api/factures/{factureId}/rappel-paiement", factureId))
                .andExpect(status().isOk());
    }



    @Test
    void genererEtSauvegarderPdf_shouldReturnPdfPath() throws Exception {
        String pdfPath = "/path/to/facture.pdf";
        when(factureService.genererEtSauvegarderPdfFacture(factureId)).thenReturn(pdfPath);

        mockMvc.perform(post("/api/factures/{factureId}/generer-pdf", factureId))
                .andExpect(status().isOk())
                .andExpect(content().string(pdfPath));
    }
}
