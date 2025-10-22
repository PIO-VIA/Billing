package com.example.account.controller;

import com.example.account.dto.request.TaxeCreateRequest;
import com.example.account.dto.request.TaxeUpdateRequest;
import com.example.account.dto.response.TaxeResponse;
import com.example.account.service.TaxeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxeController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class TaxeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaxeService taxeService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaxeResponse taxeResponse;
    private TaxeCreateRequest taxeCreateRequest;
    private TaxeUpdateRequest taxeUpdateRequest;
    private UUID taxeId;

    @BeforeEach
    void setUp() {
        taxeId = UUID.randomUUID();

        taxeResponse = new TaxeResponse();
        taxeResponse.setIdTaxe(taxeId);
        taxeResponse.setNomTaxe("TVA");
        taxeResponse.setTypeTaxe("PERCENTAGE");
        taxeResponse.setCalculTaxe(new BigDecimal("20.00"));
        taxeResponse.setActif(true);

        taxeCreateRequest = new TaxeCreateRequest();
        taxeCreateRequest.setNomTaxe("TVA");
        taxeCreateRequest.setTypeTaxe("PERCENTAGE");
        taxeCreateRequest.setCalculTaxe(new BigDecimal("20.00"));
        taxeCreateRequest.setPorteTaxe("NATIONAL");
        taxeCreateRequest.setPositionFiscale("STANDARD");
        taxeCreateRequest.setMontant(new BigDecimal("0.00"));
        taxeCreateRequest.setActif(true);

        taxeUpdateRequest = new TaxeUpdateRequest();
        taxeUpdateRequest.setCalculTaxe(new BigDecimal("21.00"));
    }

    @Test
    void createTaxe_shouldReturnCreatedTaxe() throws Exception {
        when(taxeService.createTaxe(any(TaxeCreateRequest.class))).thenReturn(taxeResponse);

        mockMvc.perform(post("/api/taxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taxeCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTaxe").value(taxeId.toString()))
                .andExpect(jsonPath("$.nomTaxe").value("TVA"));
    }

    @Test
    void updateTaxe_shouldReturnUpdatedTaxe() throws Exception {
        when(taxeService.updateTaxe(eq(taxeId), any(TaxeUpdateRequest.class))).thenReturn(taxeResponse);

        mockMvc.perform(put("/api/taxes/{taxeId}", taxeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taxeUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxeById_shouldReturnTaxe() throws Exception {
        when(taxeService.getTaxeById(taxeId)).thenReturn(taxeResponse);

        mockMvc.perform(get("/api/taxes/{taxeId}", taxeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxeByNom_shouldReturnTaxe() throws Exception {
        when(taxeService.getTaxeByNom("TVA")).thenReturn(taxeResponse);

        mockMvc.perform(get("/api/taxes/nom/{nomTaxe}", "TVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomTaxe").value("TVA"));
    }

    @Test
    void getAllTaxes_shouldReturnTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getAllTaxes()).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getAllTaxesPaginated_shouldReturnPagedTaxes() throws Exception {
        Page<TaxeResponse> taxesPage = new PageImpl<>(Collections.singletonList(taxeResponse));
        when(taxeService.getAllTaxes(any(PageRequest.class))).thenReturn(taxesPage);

        mockMvc.perform(get("/api/taxes/page")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getActiveTaxes_shouldReturnActiveTaxeList() throws Exception {
        List<TaxeResponse> activeTaxes = Collections.singletonList(taxeResponse);
        when(taxeService.getActiveTaxes()).thenReturn(activeTaxes);

        mockMvc.perform(get("/api/taxes/actives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxesByType_shouldReturnTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getTaxesByType("PERCENTAGE")).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes/type/{typeTaxe}", "PERCENTAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getActiveTaxesByType_shouldReturnActiveTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getActiveTaxesByType("PERCENTAGE")).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes/type/{typeTaxe}/actives", "PERCENTAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxesByPorte_shouldReturnTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getTaxesByPorte("NATIONAL")).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes/porte/{porteTaxe}", "NATIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxesByPositionFiscale_shouldReturnTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getTaxesByPositionFiscale("STANDARD")).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes/position/{positionFiscale}", "STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxesByCalculRange_shouldReturnTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getTaxesByCalculRange(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes/calcul-range")
                        .param("minTaux", "10.00")
                        .param("maxTaux", "30.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void getTaxesByMontantRange_shouldReturnTaxeList() throws Exception {
        List<TaxeResponse> taxes = Collections.singletonList(taxeResponse);
        when(taxeService.getTaxesByMontantRange(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(taxes);

        mockMvc.perform(get("/api/taxes/montant-range")
                        .param("minMontant", "0.00")
                        .param("maxMontant", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTaxe").value(taxeId.toString()));
    }

    @Test
    void deleteTaxe_shouldReturnNoContent() throws Exception {
        doNothing().when(taxeService).deleteTaxe(taxeId);

        mockMvc.perform(delete("/api/taxes/{taxeId}", taxeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void activerTaxe_shouldReturnActivatedTaxe() throws Exception {
        when(taxeService.activerTaxe(taxeId)).thenReturn(taxeResponse);

        mockMvc.perform(put("/api/taxes/{taxeId}/activer", taxeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTaxe").value(taxeId.toString()));
    }

    @Test
    void desactiverTaxe_shouldReturnDeactivatedTaxe() throws Exception {
        when(taxeService.desactiverTaxe(taxeId)).thenReturn(taxeResponse);

        mockMvc.perform(put("/api/taxes/{taxeId}/desactiver", taxeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTaxe").value(taxeId.toString()));
    }

    @Test
    void countActiveTaxes_shouldReturnCount() throws Exception {
        when(taxeService.countActiveTaxes()).thenReturn(5L);

        mockMvc.perform(get("/api/taxes/count/actives"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void countByType_shouldReturnCount() throws Exception {
        when(taxeService.countByType("PERCENTAGE")).thenReturn(3L);

        mockMvc.perform(get("/api/taxes/count/type/{typeTaxe}", "PERCENTAGE"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
