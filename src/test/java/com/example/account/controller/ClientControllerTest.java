package com.example.account.controller;

import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.request.ClientUpdateRequest;
import com.example.account.dto.response.ClientResponse;
import com.example.account.model.enums.TypeClient;
import com.example.account.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false) //  désactive les filtres Spring Security pendant le test
@WithMockUser //  simule un utilisateur connecté (sinon 403 Forbidden)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientResponse clientResponse;
    private ClientCreateRequest clientCreateRequest;
    private ClientUpdateRequest clientUpdateRequest;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        clientResponse = new ClientResponse();
        clientResponse.setId(clientId);
        clientResponse.setUsername("testuser");
        clientResponse.setEmail("test@example.com");

        clientCreateRequest = new ClientCreateRequest();
        clientCreateRequest.setUsername("testuser");
        clientCreateRequest.setEmail("test@example.com");
        clientCreateRequest.setPassword("password");

        clientUpdateRequest = new ClientUpdateRequest();
        clientUpdateRequest.setEmail("newemail@example.com");
    }

    @Test
    void createClient_shouldReturnCreatedClient() throws Exception {
        when(clientService.createClient(any(ClientCreateRequest.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateClient_shouldReturnUpdatedClient() throws Exception {
        when(clientService.updateClient(eq(clientId), any(ClientUpdateRequest.class))).thenReturn(clientResponse);

        mockMvc.perform(put("/api/clients/{clientId}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()));
    }

    @Test
    void getClientById_shouldReturnClient() throws Exception {
        when(clientService.getClientById(clientId)).thenReturn(clientResponse);

        mockMvc.perform(get("/api/clients/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()));
    }

    @Test
    void getClientByUsername_shouldReturnClient() throws Exception {
        when(clientService.getClientByUsername("testuser")).thenReturn(clientResponse);

        mockMvc.perform(get("/api/clients/username/{username}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getAllClients_shouldReturnClientList() throws Exception {
        List<ClientResponse> clients = Collections.singletonList(clientResponse);
        when(clientService.getAllClients()).thenReturn(clients);

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientId.toString()));
    }

    @Test
    void getActiveClients_shouldReturnActiveClientList() throws Exception {
        List<ClientResponse> activeClients = Collections.singletonList(clientResponse);
        when(clientService.getActiveClients()).thenReturn(activeClients);

        mockMvc.perform(get("/api/clients/actifs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientId.toString()));
    }

    @Test
    void getClientsByType_shouldReturnClientList() throws Exception {
        List<ClientResponse> clients = Collections.singletonList(clientResponse);
        when(clientService.getClientsByType(TypeClient.PARTICULIER)).thenReturn(clients);

        mockMvc.perform(get("/api/clients/type/{typeClient}", TypeClient.PARTICULIER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientId.toString()));
    }

    @Test
    void deleteClient_shouldReturnNoContent() throws Exception {
        doNothing().when(clientService).deleteClient(clientId);

        mockMvc.perform(delete("/api/clients/{clientId}", clientId))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateSolde_shouldReturnUpdatedClient() throws Exception {
        when(clientService.updateSolde(clientId, 100.0)).thenReturn(clientResponse);

        mockMvc.perform(put("/api/clients/{clientId}/solde", clientId)
                        .param("montant", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()));
    }

    @Test
    void countActiveClients_shouldReturnCount() throws Exception {
        when(clientService.countActiveClients()).thenReturn(5L);

        mockMvc.perform(get("/api/clients/count/actifs"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
