package com.example.account.integration;

import com.example.account.config.TestContainersConfiguration;
import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.request.ClientUpdateRequest;
import com.example.account.dto.response.ClientResponse;
import com.example.account.model.entity.Client;
import com.example.account.model.enums.TypeClient;
import com.example.account.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration E2E pour le module Client
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @AfterEach
    void tearDown() {
        clientRepository.deleteAll();
    }

    /**
     * Test 1 : Création d'un client avec succès
     */
    @Test
    @Order(1)
    @DisplayName("POST /api/clients - Doit créer un client avec succès")
    void createClient_shouldPersistInDatabase() throws Exception {
        // Given
        ClientCreateRequest request = ClientCreateRequest.builder()
                .username("jean.dupont")

                .email("jean.dupont@example.com")
                .telephone("0123456789")
                .adresse("123 Rue de la Paix")

                .typeClient(TypeClient.PARTICULIER)
                .actif(true)
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idClient").exists())
                .andExpect(jsonPath("$.username").value("jean.dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@example.com"))
                .andExpect(jsonPath("$.actif").value(true))
                .andReturn();

        // Vérifier en base de données
        ClientResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ClientResponse.class
        );

        Client clientInDb = clientRepository.findById(response.getIdClient()).orElseThrow();
        assertThat(clientInDb.getUsername()).isEqualTo("jean.dupont");
        assertThat(clientInDb.getEmail()).isEqualTo("jean.dupont@example.com");
    }

    /**
     * Test 2 : Création d'un client avec username dupliqué (erreur)
     */
    @Test
    @Order(2)
    @DisplayName("POST /api/clients - Username dupliqué doit retourner erreur")
    void createClient_duplicateUsername_shouldReturnError() throws Exception {
        // Given - Créer un premier client
        Client existing = clientRepository.save(Client.builder()
                .username("dupont.jean")
                .email("unique@example.com")
                .actif(true)
                .build());

        // When - Essayer de créer un client avec le même username
        ClientCreateRequest request = ClientCreateRequest.builder()
                .username("dupont.jean")  // Même username
                .email("autre@example.com")
                .actif(true)
                .build();

        // Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test 3 : Récupération d'un client par ID
     */
    @Test
    @Order(3)
    @DisplayName("GET /api/clients/{id} - Doit retourner le client existant")
    void getClientById_shouldReturnClient() throws Exception {
        // Given
        Client client = clientRepository.save(Client.builder()
                .username("martin.sophie")

                .email("sophie.martin@example.com")
                .actif(true)
                .soldeCourant(1500.0)
                .build());

        // When & Then
        mockMvc.perform(get("/api/clients/{clientId}", client.getIdClient()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idClient").value(client.getIdClient().toString()))
                .andExpect(jsonPath("$.username").value("martin.sophie"))
                .andExpect(jsonPath("$.email").value("sophie.martin@example.com"))
                .andExpect(jsonPath("$.soldeCourant").value(1500.0));
    }

    /**
     * Test 4 : Mise à jour d'un client
     */
    @Test
    @Order(4)
    @DisplayName("PUT /api/clients/{id} - Doit mettre à jour le client")
    void updateClient_shouldPersistChanges() throws Exception {
        // Given
        Client client = clientRepository.save(Client.builder()
                .username("bernard.paul")
                .email("paul.bernard@example.com")
                .telephone("0111111111")
                .actif(true)
                .build());

        ClientUpdateRequest updateRequest = ClientUpdateRequest.builder()
                .telephone("0699999999")  // Nouveau téléphone
                .adresse("456 Avenue des Champs")  // Nouvelle adresse
                .build();

        // When
        mockMvc.perform(put("/api/clients/{clientId}", client.getIdClient())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telephone").value("0699999999"))
                .andExpect(jsonPath("$.adresse").value("456 Avenue des Champs"));

        // Vérifier en base
        Client updated = clientRepository.findById(client.getIdClient()).orElseThrow();
        assertThat(updated.getTelephone()).isEqualTo("0699999999");
        assertThat(updated.getAdresse()).isEqualTo("456 Avenue des Champs");
    }

    /**
     * Test 5 : Suppression d'un client
     */
    @Test
    @Order(5)
    @DisplayName("DELETE /api/clients/{id} - Doit supprimer le client")
    void deleteClient_shouldRemoveFromDatabase() throws Exception {
        // Given
        Client client = clientRepository.save(Client.builder()
                .username("delete.test")
                .email("delete@test.com")
                .actif(true)
                .build());

        UUID clientId = client.getIdClient();

        // Vérifier existence
        assertThat(clientRepository.findById(clientId)).isPresent();

        // When
        mockMvc.perform(delete("/api/clients/{clientId}", clientId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Then
        assertThat(clientRepository.findById(clientId)).isEmpty();
    }

    /**
     * Test 6 : Récupération des clients actifs
     */
    @Test
    @Order(6)
    @DisplayName("GET /api/clients/actifs - Doit retourner uniquement les clients actifs")
    void getActiveClients_shouldReturnOnlyActiveClients() throws Exception {
        // Given
        clientRepository.save(Client.builder()
                .username("actif1")
                .email("actif1@test.com")
                .actif(true)
                .build());

        clientRepository.save(Client.builder()
                .username("inactif1")
                .email("inactif1@test.com")
                .actif(false)
                .build());

        clientRepository.save(Client.builder()
                .username("actif2")
                .email("actif2@test.com")
                .actif(true)
                .build());

        // When & Then
        mockMvc.perform(get("/api/clients/actifs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].actif").value(true))
                .andExpect(jsonPath("$[1].actif").value(true));
    }

    /**
     * Test 7 : Mise à jour du solde client
     */
    @Test
    @Order(7)
    @DisplayName("PUT /api/clients/{id}/solde - Doit mettre à jour le solde")
    void updateSolde_shouldUpdateClientBalance() throws Exception {
        // Given
        Client client = clientRepository.save(Client.builder()
                .username("solde.test")
                .email("solde@test.com")
                .soldeCourant(1000.0)
                .actif(true)
                .build());

        // When - Ajouter 500 au solde
        mockMvc.perform(put("/api/clients/{clientId}/solde", client.getIdClient())
                        .param("montant", "500.0"))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soldeCourant").value(1500.0));

        // Vérifier en base
        Client updated = clientRepository.findById(client.getIdClient()).orElseThrow();
        assertThat(updated.getSoldeCourant()).isEqualTo(1500.0);
    }

    /**
     * Test 8 : Récupération client par username
     */
    @Test
    @Order(8)
    @DisplayName("GET /api/clients/username/{username} - Doit retourner le client")
    void getClientByUsername_shouldReturnClient() throws Exception {
        // Given
        clientRepository.save(Client.builder()
                .username("unique.username")
                .email("unique@test.com")
                .actif(true)
                .build());

        // When & Then
        mockMvc.perform(get("/api/clients/username/{username}", "unique.username"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("unique.username"))
                .andExpect(jsonPath("$.email").value("unique@test.com"));
    }

    /**
     * Test 9 : Validation des données
     */
    @Test
    @Order(9)
    @DisplayName("POST /api/clients - Données invalides doivent retourner erreur de validation")
    void createClient_invalidData_shouldReturnValidationError() throws Exception {
        // Given - Request sans username (champ obligatoire)
        ClientCreateRequest request = ClientCreateRequest.builder()
                .email("invalide@test.com")
                // username manquant
                .build();

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Test 10 : Compter les clients actifs
     */
    @Test
    @Order(10)
    @DisplayName("GET /api/clients/count/actifs - Doit retourner le nombre de clients actifs")
    void countActiveClients_shouldReturnCorrectCount() throws Exception {
        // Given
        clientRepository.save(Client.builder().username("count1").actif(true).build());
        clientRepository.save(Client.builder().username("count2").actif(true).build());
        clientRepository.save(Client.builder().username("count3").actif(false).build());

        // When & Then
        mockMvc.perform(get("/api/clients/count/actifs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}
