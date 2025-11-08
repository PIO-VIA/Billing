package com.example.account.integration;

import com.example.account.config.TestContainersConfiguration;
import com.example.account.dto.request.PaiementCreateRequest;
import com.example.account.dto.response.PaiementResponse;
import com.example.account.model.entity.Client;
import com.example.account.model.entity.Facture;
import com.example.account.model.entity.Paiement;
import com.example.account.model.enums.StatutFacture;
import com.example.account.model.enums.TypePaiement;
import com.example.account.repository.ClientRepository;
import com.example.account.repository.FactureRepository;
import com.example.account.repository.PaiementRepository;
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
 * Tests d'intégration E2E pour le module Paiement
 *
 * Flow testé :
 * 1. Création d'un client
 * 2. Création d'une facture
 * 3. Enregistrement de paiements
 * 4. Vérification de la cohérence des données
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaiementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;
    private Facture testFacture;

    @BeforeEach
    void setUp() {
        // Créer un client de test
        testClient = clientRepository.save(Client.builder()
                .username("paiement-test-" + System.currentTimeMillis())
                .email("paiement@test.com")
                .actif(true)
                .soldeCourant(0.0)
                .build());

        // Créer une facture de test
        testFacture = factureRepository.save(Facture.builder()
                .numeroFacture("FAC-PAIEMENT-" + System.currentTimeMillis())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .idClient(testClient.getIdClient())
                .nomClient(testClient.getUsername())
                .type("VENTE")
                .etat(StatutFacture.ENVOYE)
                .montantTotal(new BigDecimal("1000.00"))
                .montantRestant(new BigDecimal("1000.00"))
                .devise("EUR")
                .build());
    }

    @AfterEach
    void tearDown() {
        paiementRepository.deleteAll();
        factureRepository.deleteAll();
        clientRepository.deleteAll();
    }

    /**
     * Test 1 : Création d'un paiement par virement
     */
    @Test
    @Order(1)
    @DisplayName("POST /api/paiements - Doit créer un paiement par virement")
    @Transactional
    void createPaiement_virement_shouldPersistInDatabase() throws Exception {
        // Given
        PaiementCreateRequest request = PaiementCreateRequest.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("500.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.VIREMENT)
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPaiement").exists())
                .andExpect(jsonPath("$.montant").value(500.00))
                .andExpect(jsonPath("$.modePaiement").value("VIREMENT"))
                .andReturn();

        // Vérifier en base
        PaiementResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PaiementResponse.class
        );

        Paiement paiementInDb = paiementRepository.findById(response.getIdPaiement()).orElseThrow();
        assertThat(paiementInDb.getMontant()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(paiementInDb.getModePaiement()).isEqualTo(TypePaiement.VIREMENT);
        assertThat(paiementInDb.getIdFacture()).isEqualTo(testFacture.getIdFacture());
    }

    /**
     * Test 2 : Création d'un paiement par carte bancaire
     */
    @Test
    @Order(2)
    @DisplayName("POST /api/paiements - Doit créer un paiement par carte")
    @Transactional
    void createPaiement_carte_shouldPersistInDatabase() throws Exception {
        // Given
        PaiementCreateRequest request = PaiementCreateRequest.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("1000.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.CARTE_BANCAIRE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.montant").value(1000.00))
                .andExpect(jsonPath("$.modePaiement").value("CARTE_BANCAIRE"));
    }

    /**
     * Test 3 : Récupération des paiements par facture
     */
    @Test
    @Order(3)
    @DisplayName("GET /api/paiements/facture/{id} - Doit retourner tous les paiements de la facture")
    @Transactional
    void getPaiementsByFacture_shouldReturnAllPayments() throws Exception {
        // Given - Créer 3 paiements pour la même facture
        for (int i = 0; i < 3; i++) {
            paiementRepository.save(Paiement.builder()
                    .idFacture(testFacture.getIdFacture())
                    .idClient(testClient.getIdClient())
                    .montant(new BigDecimal("100.00"))
                    .date(LocalDate.now())
                    .modePaiement(TypePaiement.ESPECES)
                    .journal("TEST")
                    .build());
        }

        // When & Then
        mockMvc.perform(get("/api/paiements/facture/{factureId}", testFacture.getIdFacture()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idFacture").value(testFacture.getIdFacture().toString()));
    }

    /**
     * Test 4 : Récupération des paiements par client
     */
    @Test
    @Order(4)
    @DisplayName("GET /api/paiements/client/{id} - Doit retourner tous les paiements du client")
    @Transactional
    void getPaiementsByClient_shouldReturnAllPayments() throws Exception {
        // Given
        paiementRepository.save(Paiement.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("250.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.CHEQUE)
                .journal("TEST")
                .build());

        paiementRepository.save(Paiement.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("750.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.VIREMENT)
                .journal("TEST")
                .build());

        // When & Then
        mockMvc.perform(get("/api/paiements/client/{clientId}", testClient.getIdClient()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idClient").value(testClient.getIdClient().toString()));
    }

    /**
     * Test 5 : Récupération d'un paiement par ID
     */
    @Test
    @Order(5)
    @DisplayName("GET /api/paiements/{id} - Doit retourner le paiement existant")
    @Transactional
    void getPaiementById_shouldReturnPayment() throws Exception {
        // Given
        Paiement paiement = paiementRepository.save(Paiement.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("600.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.VIREMENT)
                .journal("TEST")
                .build());

        // When & Then
        mockMvc.perform(get("/api/paiements/{paiementId}", paiement.getIdPaiement()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaiement").value(paiement.getIdPaiement().toString()))
                .andExpect(jsonPath("$.montant").value(600.00))
                .andExpect(jsonPath("$.referenceTransaction").value("REF-12345"));
    }

    /**
     * Test 6 : Filtrage des paiements par type
     */
    @Test
    @Order(6)
    @DisplayName("GET /api/paiements/type/{type} - Doit filtrer par type de paiement")
    @Transactional
    void getPaiementsByType_shouldFilterByPaymentType() throws Exception {
        // Given
        paiementRepository.save(Paiement.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("100.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.ESPECES)
                .build());

        paiementRepository.save(Paiement.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("200.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.CARTE_BANCAIRE)
                .build());

        // When & Then
        mockMvc.perform(get("/api/paiements/type/{type}", "ESPECES"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].modePaiement").value("ESPECES"));
    }

    /**
     * Test 7 : Suppression d'un paiement
     */
    @Test
    @Order(7)
    @DisplayName("DELETE /api/paiements/{id} - Doit supprimer le paiement")
    @Transactional
    void deletePaiement_shouldRemoveFromDatabase() throws Exception {
        // Given
        Paiement paiement = paiementRepository.save(Paiement.builder()
                .idFacture(testFacture.getIdFacture())
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("50.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.ESPECES)
                .build());

        UUID paiementId = paiement.getIdPaiement();

        // Vérifier existence
        assertThat(paiementRepository.findById(paiementId)).isPresent();

        // When
        mockMvc.perform(delete("/api/paiements/{paiementId}", paiementId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Then
        assertThat(paiementRepository.findById(paiementId)).isEmpty();
    }

    /**
     * Test 8 : Flow complet - Facture avec plusieurs paiements partiels
     *
     * Scénario :
     * 1. Créer une facture de 1000 EUR
     * 2. Paiement 1 : 300 EUR
     * 3. Paiement 2 : 400 EUR
     * 4. Paiement 3 : 300 EUR (paiement final)
     * 5. Vérifier que la facture est PAYEE
     */
    @Test
    @Order(8)
    @DisplayName("Flow complet - Plusieurs paiements partiels jusqu'au paiement complet")
    @Transactional
    void fullPaymentFlow_multiplePartialPayments_shouldUpdateInvoiceStatus() throws Exception {
        // Given - Facture de 1000 EUR
        UUID factureId = testFacture.getIdFacture();

        // When - Paiement 1 : 300 EUR
        PaiementCreateRequest paiement1 = PaiementCreateRequest.builder()
                .idFacture(factureId)
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("300.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.VIREMENT)
                .build();

        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiement1)))
                .andExpect(status().isCreated());

        // Mettre à jour manuellement la facture (normalement fait par le consumer)
        mockMvc.perform(put("/api/factures/{id}/paiement", factureId)
                        .param("montantPaye", "300.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantRestant").value(700.00))
                .andExpect(jsonPath("$.etat").value("PARTIELLEMENT_PAYE"));

        // Paiement 2 : 400 EUR
        PaiementCreateRequest paiement2 = PaiementCreateRequest.builder()
                .idFacture(factureId)
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("400.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.CARTE_BANCAIRE)
                .build();

        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiement2)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/factures/{id}/paiement", factureId)
                        .param("montantPaye", "400.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantRestant").value(300.00))
                .andExpect(jsonPath("$.etat").value("PARTIELLEMENT_PAYE"));

        // Paiement 3 (final) : 300 EUR
        PaiementCreateRequest paiement3 = PaiementCreateRequest.builder()
                .idFacture(factureId)
                .idClient(testClient.getIdClient())
                .montant(new BigDecimal("300.00"))
                .date(LocalDate.now())
                .modePaiement(TypePaiement.CHEQUE)
                .build();

        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paiement3)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/factures/{id}/paiement", factureId)
                        .param("montantPaye", "300.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantRestant").value(0))
                .andExpect(jsonPath("$.etat").value("PAYE"));

        // Then - Vérifier qu'on a bien 3 paiements pour cette facture
        List<Paiement> paiements = paiementRepository.findByIdFacture(factureId);
        assertThat(paiements).hasSize(3);

        // Vérifier le total payé
        BigDecimal totalPaye = paiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalPaye).isEqualByComparingTo(new BigDecimal("1000.00"));

        // Vérifier la facture
        Facture facture = factureRepository.findById(factureId).orElseThrow();
        assertThat(facture.getEtat()).isEqualTo(StatutFacture.PAYE);
        assertThat(facture.getMontantRestant()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
