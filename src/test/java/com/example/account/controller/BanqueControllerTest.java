package com.example.account.controller;

import com.example.account.dto.request.BanqueCreateRequest;
import com.example.account.dto.request.BanqueUpdateRequest;
import com.example.account.dto.response.BanqueResponse;
import com.example.account.service.BanqueService;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BanqueController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class BanqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BanqueService banqueService;

    @Autowired
    private ObjectMapper objectMapper;

    private BanqueResponse banqueResponse;
    private BanqueCreateRequest banqueCreateRequest;
    private BanqueUpdateRequest banqueUpdateRequest;
    private UUID banqueId;

    @BeforeEach
    void setUp() {
        banqueId = UUID.randomUUID();

        banqueResponse = BanqueResponse.builder()
                .idBanque(banqueId)
                .numeroCompte("FR7630001007941234567890185")
                .banque("BNP Paribas")
                .build();

        banqueCreateRequest = BanqueCreateRequest.builder()
                .numeroCompte("FR7630001007941234567890185")
                .banque("BNP Paribas")
                .build();

        banqueUpdateRequest = BanqueUpdateRequest.builder()
                .numeroCompte("FR7630001007941234567890186")
                .banque("BNP Paribas Fortis")
                .build();
    }

    @Test
    void createBanque_shouldReturnCreatedBanque() throws Exception {
        when(banqueService.createBanque(any(BanqueCreateRequest.class))).thenReturn(banqueResponse);

        mockMvc.perform(post("/api/banques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(banqueCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idBanque").value(banqueId.toString()))
                .andExpect(jsonPath("$.numeroCompte").value("FR7630001007941234567890185"))
                .andExpect(jsonPath("$.banque").value("BNP Paribas"));
    }

    @Test
    void updateBanque_shouldReturnUpdatedBanque() throws Exception {
        when(banqueService.updateBanque(eq(banqueId), any(BanqueUpdateRequest.class))).thenReturn(banqueResponse);

        mockMvc.perform(put("/api/banques/{banqueId}", banqueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(banqueUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBanque").value(banqueId.toString()));
    }

    @Test
    void getBanqueById_shouldReturnBanque() throws Exception {
        when(banqueService.getBanqueById(banqueId)).thenReturn(banqueResponse);

        mockMvc.perform(get("/api/banques/{banqueId}", banqueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBanque").value(banqueId.toString()))
                .andExpect(jsonPath("$.banque").value("BNP Paribas"));
    }

    @Test
    void getBanqueByNumeroCompte_shouldReturnBanque() throws Exception {
        when(banqueService.getBanqueByNumeroCompte("FR7630001007941234567890185")).thenReturn(banqueResponse);

        mockMvc.perform(get("/api/banques/numero-compte/{numeroCompte}", "FR7630001007941234567890185"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCompte").value("FR7630001007941234567890185"));
    }

    @Test
    void getAllBanques_shouldReturnBanqueList() throws Exception {
        List<BanqueResponse> banques = Collections.singletonList(banqueResponse);
        when(banqueService.getAllBanques()).thenReturn(banques);

        mockMvc.perform(get("/api/banques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idBanque").value(banqueId.toString()));
    }

    @Test
    void getAllBanquesPaginated_shouldReturnPaginatedBanqueList() throws Exception {
        Page<BanqueResponse> banques = new PageImpl<>(Collections.singletonList(banqueResponse));
        when(banqueService.getAllBanques(any(Pageable.class))).thenReturn(banques);

        mockMvc.perform(get("/api/banques/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idBanque").value(banqueId.toString()));
    }

    @Test
    void getBanquesByName_shouldReturnBanqueList() throws Exception {
        List<BanqueResponse> banques = Collections.singletonList(banqueResponse);
        when(banqueService.getBanquesByName("BNP Paribas")).thenReturn(banques);

        mockMvc.perform(get("/api/banques/nom/{nomBanque}", "BNP Paribas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].banque").value("BNP Paribas"));
    }

    @Test
    void searchBanquesByName_shouldReturnBanqueList() throws Exception {
        List<BanqueResponse> banques = Collections.singletonList(banqueResponse);
        when(banqueService.searchBanquesByName("BNP")).thenReturn(banques);

        mockMvc.perform(get("/api/banques/search/nom")
                        .param("nom", "BNP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].banque").value("BNP Paribas"));
    }

    @Test
    void searchBanquesByNumeroCompte_shouldReturnBanqueList() throws Exception {
        List<BanqueResponse> banques = Collections.singletonList(banqueResponse);
        when(banqueService.searchBanquesByNumeroCompte("FR763")).thenReturn(banques);

        mockMvc.perform(get("/api/banques/search/numero-compte")
                        .param("numeroCompte", "FR763"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroCompte").value("FR7630001007941234567890185"));
    }

    @Test
    void deleteBanque_shouldReturnNoContent() throws Exception {
        doNothing().when(banqueService).deleteBanque(banqueId);

        mockMvc.perform(delete("/api/banques/{banqueId}", banqueId))
                .andExpect(status().isNoContent());
    }
}
