package com.example.account.integration;

import com.example.account.config.TestContainersConfiguration;
import com.example.account.dto.request.FactureCreateRequest;
import com.example.account.dto.response.FactureResponse;
import com.example.account.model.entity.Client;
import com.example.account.model.entity.Facture;
import com.example.account.model.enums.StatutFacture;
import com.example.account.repository.ClientRepository;
import com.example.account.repository.FactureRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration E2E pour le module Facture
 *
 * Ces tests vérifient :
 * - L'intégration complète de toutes les couches (Controller → Service → Repository → Database)
 * - La persistance réelle en base de données PostgreSQL (via TestContainers)
 * - Les transactions et rollbacks
 * - Les validations
 * - Les cas d'erreur
 * - Les flows métier complets (création, modification, paiement)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FactureIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        // Créer un client de test pour chaque test
        testClient = Client.builder()
                .username("client-test-" + System.currentTimeMillis())
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@test.com")
                .telephone("0123456789")
                .adresse("123 Rue de Test")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .actif(true)
                .soldeCourant(0.0)
                .build();

        testClient = clientRepository.save(testClient);
        clientId = testClient.getIdClient();
    }

    @AfterEach
    void tearDown() {
        // Nettoyage après chaque test
        factureRepository.deleteAll();
        clientRepository.deleteAll();
    }

    /**
     * Test 1 : Création d'une facture avec succès
     *
     * Vérifie que :
     * - La facture est créée en base de données
     * - Un numéro de facture est généré automatiquement
     * - Les montants sont initialisés correctement
     * - Le statut initial est BROUILLON
     * - Le client est bien associé
     */
    @Test
    @Order(1)
    @DisplayName("POST /api/factures - Doit créer une facture avec succès")
    void createFacture_shouldPersistInDatabaseAndReturnCreated() throws Exception {
        // Given
        FactureCreateRequest request = FactureCreateRequest.builder()
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .type("VENTE")
                .etat(StatutFacture.BROUILLON)
                .devise("EUR")
                .tauxChange(BigDecimal.ONE)
                .conditionsPaiement("30 jours")
                .notes("Facture de test")
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/factures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Then - HTTP Response
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFacture").exists())
                .andExpect(jsonPath("$.numeroFacture").exists())
                .andExpect(jsonPath("$.numeroFacture").value(org.hamcrest.Matchers.startsWith("FAC-")))
                .andExpect(jsonPath("$.idClient").value(clientId.toString()))
                .andExpect(jsonPath("$.etat").value("BROUILLON"))
                .andExpect(jsonPath("$.type").value("VENTE"))
                .andExpect(jsonPath("$.devise").value("EUR"))
                .andReturn();

        // Then - Database Persistence
        FactureResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FactureResponse.class
        );

        // Vérifier que la facture existe bien en base de données
        Facture factureInDb = factureRepository.findById(response.getIdFacture())
                .orElseThrow(() -> new AssertionError("Facture devrait être en base de données"));

        assertThat(factureInDb).isNotNull();
        assertThat(factureInDb.getNumeroFacture()).isEqualTo(response.getNumeroFacture());
        assertThat(factureInDb.getIdClient()).isEqualTo(clientId);
        assertThat(factureInDb.getEtat()).isEqualTo(StatutFacture.BROUILLON);
        assertThat(factureInDb.getCreatedAt()).isNotNull();
    }

    /**
     * Test 2 : Récupération d'une facture par ID
     */
    @Test
    @Order(2)
    @DisplayName("GET /api/factures/{id} - Doit retourner la facture existante")
    void getFactureById_shouldReturnExistingFacture() throws Exception {
        // Given - Créer une facture en base
        Facture facture = Facture.builder()
                .numeroFacture("FAC-TEST-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.BROUILLON)
                .montantTotal(new BigDecimal("1000.00"))
                .montantRestant(new BigDecimal("1000.00"))
                .devise("EUR")
                .build();

        facture = factureRepository.save(facture);

        // When & Then
        mockMvc.perform(get("/api/factures/{factureId}", facture.getIdFacture()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFacture").value(facture.getIdFacture().toString()))
                .andExpect(jsonPath("$.numeroFacture").value(facture.getNumeroFacture()))
                .andExpect(jsonPath("$.idClient").value(clientId.toString()))
                .andExpect(jsonPath("$.montantTotal").value(1000.00))
                .andExpect(jsonPath("$.etat").value("BROUILLON"));
    }

    /**
     * Test 3 : Enregistrement d'un paiement partiel
     *
     * Scénario :
     * 1. Créer une facture de 1000 EUR
     * 2. Enregistrer un paiement de 400 EUR
     * 3. Vérifier que le montant restant = 600 EUR
     * 4. Vérifier que le statut = PARTIELLEMENT_PAYE
     */
    @Test
    @Order(3)
    @DisplayName("PUT /api/factures/{id}/paiement - Paiement partiel doit mettre à jour le statut")
    @Transactional
    void enregistrerPaiement_partiel_shouldUpdateStatus() throws Exception {
        // Given - Facture de 1000 EUR
        Facture facture = Facture.builder()
                .numeroFacture("FAC-TEST-PAIEMENT-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.ENVOYE)
                .montantTotal(new BigDecimal("1000.00"))
                .montantRestant(new BigDecimal("1000.00"))
                .devise("EUR")
                .build();

        facture = factureRepository.save(facture);
        UUID factureId = facture.getIdFacture();

        // When - Paiement partiel de 400 EUR
        mockMvc.perform(put("/api/factures/{id}/paiement", factureId)
                        .param("montantPaye", "400.00"))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantRestant").value(600.00))
                .andExpect(jsonPath("$.etat").value("PARTIELLEMENT_PAYE"));

        // Vérifier en base de données
        Facture factureUpdated = factureRepository.findById(factureId).orElseThrow();
        assertThat(factureUpdated.getMontantRestant()).isEqualByComparingTo(new BigDecimal("600.00"));
        assertThat(factureUpdated.getEtat()).isEqualTo(StatutFacture.PARTIELLEMENT_PAYE);
    }

    /**
     * Test 4 : Enregistrement d'un paiement complet
     *
     * Scénario :
     * 1. Créer une facture de 1000 EUR
     * 2. Payer 1000 EUR
     * 3. Vérifier que montant restant = 0
     * 4. Vérifier que statut = PAYE
     */
    @Test
    @Order(4)
    @DisplayName("PUT /api/factures/{id}/paiement - Paiement complet doit marquer comme PAYE")
    @Transactional
    void enregistrerPaiement_complet_shouldMarkAsPaid() throws Exception {
        // Given
        Facture facture = Facture.builder()
                .numeroFacture("FAC-TEST-COMPLET-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.ENVOYE)
                .montantTotal(new BigDecimal("1000.00"))
                .montantRestant(new BigDecimal("1000.00"))
                .devise("EUR")
                .build();

        facture = factureRepository.save(facture);

        // When - Paiement complet
        mockMvc.perform(put("/api/factures/{id}/paiement", facture.getIdFacture())
                        .param("montantPaye", "1000.00"))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantRestant").value(0))
                .andExpect(jsonPath("$.etat").value("PAYE"));

        // Vérifier en base
        Facture factureUpdated = factureRepository.findById(facture.getIdFacture()).orElseThrow();
        assertThat(factureUpdated.getMontantRestant()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(factureUpdated.getEtat()).isEqualTo(StatutFacture.PAYE);
    }

    /**
     * Test 5 : Paiement avec montant supérieur au restant (cas d'erreur)
     */
    @Test
    @Order(5)
    @DisplayName("PUT /api/factures/{id}/paiement - Paiement excessif doit retourner erreur 400")
    @Transactional
    void enregistrerPaiement_excessif_shouldReturnBadRequest() throws Exception {
        // Given
        Facture facture = Facture.builder()
                .numeroFacture("FAC-TEST-EXCESSIF-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.ENVOYE)
                .montantTotal(new BigDecimal("1000.00"))
                .montantRestant(new BigDecimal("1000.00"))
                .devise("EUR")
                .build();

        facture = factureRepository.save(facture);

        // When & Then - Paiement de 1500 EUR (> 1000 EUR restant)
        mockMvc.perform(put("/api/factures/{id}/paiement", facture.getIdFacture())
                        .param("montantPaye", "1500.00"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test 6 : Récupération de factures par client
     */
    @Test
    @Order(6)
    @DisplayName("GET /api/factures/client/{clientId} - Doit retourner toutes les factures du client")
    void getFacturesByClient_shouldReturnAllClientInvoices() throws Exception {
        // Given - Créer 3 factures pour le même client
        for (int i = 0; i < 3; i++) {
            Facture facture = Facture.builder()
                    .numeroFacture("FAC-TEST-CLIENT-" + i + "-" + System.currentTimeMillis())
                    .dateFacturation(LocalDate.now())
                    .dateEcheance(LocalDate.now().plusDays(30))
                    .idClient(clientId)
                    .nomClient(testClient.getUsername())
                    .type("VENTE")
                    .etat(StatutFacture.BROUILLON)
                    .montantTotal(new BigDecimal("100.00"))
                    .montantRestant(new BigDecimal("100.00"))
                    .build();
            factureRepository.save(facture);
        }

        // When & Then
        mockMvc.perform(get("/api/factures/client/{clientId}", clientId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idClient").value(clientId.toString()))
                .andExpect(jsonPath("$[1].idClient").value(clientId.toString()))
                .andExpect(jsonPath("$[2].idClient").value(clientId.toString()));
    }

    /**
     * Test 7 : Suppression d'une facture
     */
    @Test
    @Order(7)
    @DisplayName("DELETE /api/factures/{id} - Doit supprimer la facture")
    @Transactional
    void deleteFacture_shouldRemoveFromDatabase() throws Exception {
        // Given
        Facture facture = Facture.builder()
                .numeroFacture("FAC-TEST-DELETE-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.BROUILLON)
                .montantTotal(new BigDecimal("100.00"))
                .montantRestant(new BigDecimal("100.00"))
                .build();

        facture = factureRepository.save(facture);
        UUID factureId = facture.getIdFacture();

        // Vérifier que la facture existe
        assertThat(factureRepository.findById(factureId)).isPresent();

        // When
        mockMvc.perform(delete("/api/factures/{id}", factureId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Then - Vérifier que la facture n'existe plus
        assertThat(factureRepository.findById(factureId)).isEmpty();
    }

    /**
     * Test 8 : Récupération d'une facture inexistante (404)
     */
    @Test
    @Order(8)
    @DisplayName("GET /api/factures/{id} - Facture inexistante doit retourner 404 ou 4xx")
    void getFactureById_nonExistent_shouldReturnNotFound() throws Exception {
        // Given - ID qui n'existe pas
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/factures/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test 9 : Marquer une facture comme payée
     */
    @Test
    @Order(9)
    @DisplayName("PUT /api/factures/{id}/marquer-paye - Doit marquer la facture comme PAYE")
    @Transactional
    void marquerCommePaye_shouldUpdateStatusToPaid() throws Exception {
        // Given
        Facture facture = Facture.builder()
                .numeroFacture("FAC-TEST-MARQUER-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.ENVOYE)
                .montantTotal(new BigDecimal("500.00"))
                .montantRestant(new BigDecimal("500.00"))
                .build();

        facture = factureRepository.save(facture);

        // When
        mockMvc.perform(put("/api/factures/{id}/marquer-paye", facture.getIdFacture()))
                .andDo(print())
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etat").value("PAYE"))
                .andExpect(jsonPath("$.montantRestant").value(0));

        // Vérifier en base
        Facture updated = factureRepository.findById(facture.getIdFacture()).orElseThrow();
        assertThat(updated.getEtat()).isEqualTo(StatutFacture.PAYE);
        assertThat(updated.getMontantRestant()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /**
     * Test 10 : Filtrer les factures par statut
     */
    @Test
    @Order(10)
    @DisplayName("GET /api/factures/etat/{etat} - Doit filtrer les factures par statut")
    void getFacturesByEtat_shouldFilterByStatus() throws Exception {
        // Given - Créer des factures avec différents statuts
        factureRepository.save(Facture.builder()
                .numeroFacture("FAC-BROUILLON-1")
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .etat(StatutFacture.BROUILLON)
                .montantTotal(BigDecimal.TEN)
                .montantRestant(BigDecimal.TEN)
                .build());

        factureRepository.save(Facture.builder()
                .numeroFacture("FAC-PAYE-1")
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(clientId)
                .etat(StatutFacture.PAYE)
                .montantTotal(BigDecimal.TEN)
                .montantRestant(BigDecimal.ZERO)
                .build());

        // When & Then - Filtrer les factures BROUILLON
        mockMvc.perform(get("/api/factures/etat/{etat}", "BROUILLON"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].etat").value("BROUILLON"));
    }
}
