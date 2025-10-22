package com.example.account.controller;

import com.example.account.dto.request.FournisseurCreateRequest;
import com.example.account.dto.request.FournisseurUpdateRequest;
import com.example.account.dto.response.FournisseurResponse;
import com.example.account.service.FournisseurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

@WebMvcTest(FournisseurController.class)
class FournisseurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FournisseurService fournisseurService;

    @Autowired
    private ObjectMapper objectMapper;

    private FournisseurResponse fournisseurResponse;
    private FournisseurCreateRequest fournisseurCreateRequest;
    private FournisseurUpdateRequest fournisseurUpdateRequest;
    private UUID fournisseurId;

    @BeforeEach
    void setUp() {
        fournisseurId = UUID.randomUUID();

        fournisseurResponse = new FournisseurResponse();
        fournisseurResponse.setIdFournisseur(fournisseurId);
        fournisseurResponse.setUsername("fournisseur_test");
        fournisseurResponse.setEmail("fournisseur@example.com");

        fournisseurCreateRequest = new FournisseurCreateRequest();
        fournisseurCreateRequest.setUsername("fournisseur_test");
        fournisseurCreateRequest.setEmail("fournisseur@example.com");

        fournisseurUpdateRequest = new FournisseurUpdateRequest();
        fournisseurUpdateRequest.setEmail("newemail_fournisseur@example.com");
    }

    @Test
    void createFournisseur_shouldReturnCreatedFournisseur() throws Exception {
        when(fournisseurService.createFournisseur(any(FournisseurCreateRequest.class))).thenReturn(fournisseurResponse);

        mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseurCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fournisseurId.toString()))
                .andExpect(jsonPath("$.username").value("fournisseur_test"));
    }

    @Test
    void updateFournisseur_shouldReturnUpdatedFournisseur() throws Exception {
        when(fournisseurService.updateFournisseur(eq(fournisseurId), any(FournisseurUpdateRequest.class))).thenReturn(fournisseurResponse);

        mockMvc.perform(put("/api/fournisseurs/{fournisseurId}", fournisseurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseurUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fournisseurId.toString()));
    }

    @Test
    void getFournisseurById_shouldReturnFournisseur() throws Exception {
        when(fournisseurService.getFournisseurById(fournisseurId)).thenReturn(fournisseurResponse);

        mockMvc.perform(get("/api/fournisseurs/{fournisseurId}", fournisseurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fournisseurId.toString()));
    }

    @Test
    void getFournisseurByUsername_shouldReturnFournisseur() throws Exception {
        when(fournisseurService.getFournisseurByUsername("fournisseur_test")).thenReturn(fournisseurResponse);

        mockMvc.perform(get("/api/fournisseurs/username/{username}", "fournisseur_test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("fournisseur_test"));
    }

    @Test
    void getAllFournisseurs_shouldReturnFournisseurList() throws Exception {
        List<FournisseurResponse> fournisseurs = Collections.singletonList(fournisseurResponse);
        when(fournisseurService.getAllFournisseurs()).thenReturn(fournisseurs);

        mockMvc.perform(get("/api/fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fournisseurId.toString()));
    }

    @Test
    void getActiveFournisseurs_shouldReturnActiveFournisseurList() throws Exception {
        List<FournisseurResponse> activeFournisseurs = Collections.singletonList(fournisseurResponse);
        when(fournisseurService.getActiveFournisseurs()).thenReturn(activeFournisseurs);

        mockMvc.perform(get("/api/fournisseurs/actifs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fournisseurId.toString()));
    }

    @Test
    void getFournisseursByCategorie_shouldReturnFournisseurList() throws Exception {
        List<FournisseurResponse> fournisseurs = Collections.singletonList(fournisseurResponse);
        when(fournisseurService.getFournisseursByCategorie("IT")).thenReturn(fournisseurs);

        mockMvc.perform(get("/api/fournisseurs/categorie/{categorie}", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fournisseurId.toString()));
    }

    @Test
    void deleteFournisseur_shouldReturnNoContent() throws Exception {
        doNothing().when(fournisseurService).deleteFournisseur(fournisseurId);

        mockMvc.perform(delete("/api/fournisseurs/{fournisseurId}", fournisseurId))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateSolde_shouldReturnUpdatedFournisseur() throws Exception {
        when(fournisseurService.updateSolde(fournisseurId, 250.0)).thenReturn(fournisseurResponse);

        mockMvc.perform(put("/api/fournisseurs/{fournisseurId}/solde", fournisseurId)
                        .param("montant", "250.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fournisseurId.toString()));
    }

    @Test
    void desactiverFournisseur_shouldReturnUpdatedFournisseur() throws Exception {
        when(fournisseurService.desactiverFournisseur(fournisseurId)).thenReturn(fournisseurResponse);

        mockMvc.perform(put("/api/fournisseurs/{fournisseurId}/desactiver", fournisseurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fournisseurId.toString()));
    }

    @Test
    void activerFournisseur_shouldReturnUpdatedFournisseur() throws Exception {
        when(fournisseurService.activerFournisseur(fournisseurId)).thenReturn(fournisseurResponse);

        mockMvc.perform(put("/api/fournisseurs/{fournisseurId}/activer", fournisseurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fournisseurId.toString()));
    }

    @Test
    void countActiveFournisseurs_shouldReturnCount() throws Exception {
        when(fournisseurService.countActiveFournisseurs()).thenReturn(3L);

        mockMvc.perform(get("/api/fournisseurs/count/actifs"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
