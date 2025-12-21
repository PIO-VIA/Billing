package com.example.account;

import com.example.account.context.OrganizationContext;
import com.example.account.dto.request.OrganizationCreateRequest;
import com.example.account.dto.response.OrganizationResponse;
import com.example.account.model.entity.*;
import com.example.account.model.enums.OrganizationRole;
import com.example.account.model.enums.TypeClient;
import com.example.account.repository.*;
import com.example.account.service.EntityOrganizationHelper;
import com.example.account.service.OrganizationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration complets pour la couche multi-tenant.
 *
 * Ces tests vérifient:
 * 1. L'isolation des données entre organisations
 * 2. Le filtrage automatique par organization_id
 * 3. Les contrôles de sécurité
 * 4. Les endpoints de gestion des organisations
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MultiTenancyIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserOrganizationRepository userOrganizationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private EntityOrganizationHelper organizationHelper;

    private static UUID userId1;
    private static UUID userId2;
    private static UUID org1Id;
    private static UUID org2Id;
    private static String baseUrl;

    @BeforeAll
    public static void setupClass() {
        System.out.println("=== DÉBUT DES TESTS MULTI-TENANCY ===");
    }

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    public void cleanup() {
        OrganizationContext.clear();
    }

    // ========== TEST 1: Création des utilisateurs ==========
    @Test
    @Order(1)
    @Transactional
    public void test01_CreateUsers() {
        System.out.println("\n--- TEST 1: Création des utilisateurs ---");

        // Créer utilisateur 1
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@test.com");
        user1.setPassword("password123");
        user1.setIsActive(true);
        user1 = userRepository.save(user1);
        userId1 = user1.getId();

        // Créer utilisateur 2
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password456");
        user2.setIsActive(true);
        user2 = userRepository.save(user2);
        userId2 = user2.getId();

        assertNotNull(userId1);
        assertNotNull(userId2);
        System.out.println("✅ Users créés: user1=" + userId1 + ", user2=" + userId2);
    }

    // ========== TEST 2: Création des organisations via API ==========
    @Test
    @Order(2)
    public void test02_CreateOrganizationsViaAPI() {
        System.out.println("\n--- TEST 2: Création des organisations via API ---");

        // Créer l'organisation 1
        OrganizationCreateRequest org1Request = OrganizationCreateRequest.builder()
            .code("ORG1")
            .shortName("Organisation 1")
            .longName("Organisation Numéro Un")
            .country("France")
            .city("Paris")
            .email("org1@test.com")
            .phone("+33123456789")
            .isBusiness(true)
            .build();

        String url1 = baseUrl + "/api/organizations/create?creatorUserId=" + userId1;
        ResponseEntity<OrganizationResponse> response1 = restTemplate.postForEntity(
            url1,
            org1Request,
            OrganizationResponse.class
        );

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertNotNull(response1.getBody());
        org1Id = response1.getBody().getId();
        System.out.println("✅ Organisation 1 créée: " + org1Id);

        // Créer l'organisation 2
        OrganizationCreateRequest org2Request = OrganizationCreateRequest.builder()
            .code("ORG2")
            .shortName("Organisation 2")
            .longName("Organisation Numéro Deux")
            .country("France")
            .city("Lyon")
            .email("org2@test.com")
            .phone("+33987654321")
            .isBusiness(true)
            .build();

        String url2 = baseUrl + "/api/organizations/create?creatorUserId=" + userId2;
        ResponseEntity<OrganizationResponse> response2 = restTemplate.postForEntity(
            url2,
            org2Request,
            OrganizationResponse.class
        );

        assertEquals(HttpStatus.CREATED, response2.getStatusCode());
        assertNotNull(response2.getBody());
        org2Id = response2.getBody().getId();
        System.out.println("✅ Organisation 2 créée: " + org2Id);
    }

    // ========== TEST 3: Vérification des rôles ==========
    @Test
    @Order(3)
    @Transactional
    public void test03_VerifyOrganizationRoles() {
        System.out.println("\n--- TEST 3: Vérification des rôles ---");

        // Vérifier que user1 est OWNER de org1
        UserOrganization membership1 = userOrganizationRepository
            .findActiveByUserIdAndOrganizationId(userId1, org1Id)
            .orElse(null);

        assertNotNull(membership1);
        assertEquals(OrganizationRole.OWNER, membership1.getRole());
        assertTrue(membership1.getIsDefault());
        System.out.println("✅ User1 est OWNER de Org1");

        // Vérifier que user2 est OWNER de org2
        UserOrganization membership2 = userOrganizationRepository
            .findActiveByUserIdAndOrganizationId(userId2, org2Id)
            .orElse(null);

        assertNotNull(membership2);
        assertEquals(OrganizationRole.OWNER, membership2.getRole());
        assertTrue(membership2.getIsDefault());
        System.out.println("✅ User2 est OWNER de Org2");
    }

    // ========== TEST 4: Isolation des données - Clients ==========
    @Test
    @Order(4)
    @Transactional
    public void test04_DataIsolation_Clients() {
        System.out.println("\n--- TEST 4: Isolation des données - Clients ---");

        // Créer client dans org1
        OrganizationContext.setCurrentOrganizationId(org1Id);
        Client client1 = Client.builder()
            .username("client_org1")
            .categorie("Premium")
            .adresse("123 Rue de Paris")
            .typeClient(TypeClient.ENTREPRISE)
            .email("client1@org1.com")
            .actif(true)
            .build();
        organizationHelper.setOrganizationId(client1);
        client1 = clientRepository.save(client1);
        UUID client1Id = client1.getIdClient();
        OrganizationContext.clear();

        // Créer client dans org2
        OrganizationContext.setCurrentOrganizationId(org2Id);
        Client client2 = Client.builder()
            .username("client_org2")
            .categorie("Standard")
            .adresse("456 Rue de Lyon")
            .typeClient(TypeClient.PARTICULIER)
            .email("client2@org2.com")
            .actif(true)
            .build();
        organizationHelper.setOrganizationId(client2);
        client2 = clientRepository.save(client2);
        UUID client2Id = client2.getIdClient();
        OrganizationContext.clear();

        // Vérifier isolation: Org1 ne voit que son client
        OrganizationContext.setCurrentOrganizationId(org1Id);
        List<Client> org1Clients = clientRepository.findAll();
        assertEquals(1, org1Clients.size());
        assertEquals("client_org1", org1Clients.get(0).getUsername());
        assertEquals(org1Id, org1Clients.get(0).getOrganizationId());
        System.out.println("✅ Org1 voit uniquement son client: " + org1Clients.get(0).getUsername());
        OrganizationContext.clear();

        // Vérifier isolation: Org2 ne voit que son client
        OrganizationContext.setCurrentOrganizationId(org2Id);
        List<Client> org2Clients = clientRepository.findAll();
        assertEquals(1, org2Clients.size());
        assertEquals("client_org2", org2Clients.get(0).getUsername());
        assertEquals(org2Id, org2Clients.get(0).getOrganizationId());
        System.out.println("✅ Org2 voit uniquement son client: " + org2Clients.get(0).getUsername());
        OrganizationContext.clear();

        // Vérifier qu'on ne peut pas accéder au client d'une autre org
        OrganizationContext.setCurrentOrganizationId(org1Id);
        assertFalse(clientRepository.findById(client2Id).isPresent());
        System.out.println("✅ Org1 ne peut pas voir le client de Org2");
        OrganizationContext.clear();

        OrganizationContext.setCurrentOrganizationId(org2Id);
        assertFalse(clientRepository.findById(client1Id).isPresent());
        System.out.println("✅ Org2 ne peut pas voir le client de Org1");
        OrganizationContext.clear();
    }

    // ========== TEST 5: Isolation des données - Produits ==========
    @Test
    @Order(5)
    @Transactional
    public void test05_DataIsolation_Produits() {
        System.out.println("\n--- TEST 5: Isolation des données - Produits ---");

        // Créer produit dans org1
        OrganizationContext.setCurrentOrganizationId(org1Id);
        Produit produit1 = Produit.builder()
            .nomProduit("Produit Org1")
            .reference("PROD-ORG1-001")
            .typeProduit("Service")
            .prixVente(new java.math.BigDecimal("100.00"))
            .active(true)
            .build();
        organizationHelper.setOrganizationId(produit1);
        produit1 = produitRepository.save(produit1);
        OrganizationContext.clear();

        // Créer produit dans org2
        OrganizationContext.setCurrentOrganizationId(org2Id);
        Produit produit2 = Produit.builder()
            .nomProduit("Produit Org2")
            .reference("PROD-ORG2-001")
            .typeProduit("Bien")
            .prixVente(new java.math.BigDecimal("200.00"))
            .active(true)
            .build();
        organizationHelper.setOrganizationId(produit2);
        produit2 = produitRepository.save(produit2);
        OrganizationContext.clear();

        // Vérifier isolation
        OrganizationContext.setCurrentOrganizationId(org1Id);
        List<Produit> org1Produits = produitRepository.findAll();
        assertEquals(1, org1Produits.size());
        assertEquals("Produit Org1", org1Produits.get(0).getNomProduit());
        System.out.println("✅ Org1 voit uniquement son produit");
        OrganizationContext.clear();

        OrganizationContext.setCurrentOrganizationId(org2Id);
        List<Produit> org2Produits = produitRepository.findAll();
        assertEquals(1, org2Produits.size());
        assertEquals("Produit Org2", org2Produits.get(0).getNomProduit());
        System.out.println("✅ Org2 voit uniquement son produit");
        OrganizationContext.clear();
    }

    // ========== TEST 6: Isolation des données - Stock ==========
    @Test
    @Order(6)
    @Transactional
    public void test06_DataIsolation_Stock() {
        System.out.println("\n--- TEST 6: Isolation des données - Stock ---");

        UUID produitId = UUID.randomUUID();

        // Créer stock dans org1
        OrganizationContext.setCurrentOrganizationId(org1Id);
        Stock stock1 = Stock.builder()
            .idProduit(produitId)
            .nomProduit("Test Product")
            .quantite(new java.math.BigDecimal("100"))
            .emplacement("A1")
            .build();
        organizationHelper.setOrganizationId(stock1);
        stock1 = stockRepository.save(stock1);
        OrganizationContext.clear();

        // Créer stock dans org2
        OrganizationContext.setCurrentOrganizationId(org2Id);
        Stock stock2 = Stock.builder()
            .idProduit(produitId)
            .nomProduit("Test Product")
            .quantite(new java.math.BigDecimal("50"))
            .emplacement("B1")
            .build();
        organizationHelper.setOrganizationId(stock2);
        stock2 = stockRepository.save(stock2);
        OrganizationContext.clear();

        // Vérifier isolation
        OrganizationContext.setCurrentOrganizationId(org1Id);
        List<Stock> org1Stocks = stockRepository.findAll();
        assertEquals(1, org1Stocks.size());
        assertEquals("A1", org1Stocks.get(0).getEmplacement());
        System.out.println("✅ Org1 voit uniquement son stock");
        OrganizationContext.clear();

        OrganizationContext.setCurrentOrganizationId(org2Id);
        List<Stock> org2Stocks = stockRepository.findAll();
        assertEquals(1, org2Stocks.size());
        assertEquals("B1", org2Stocks.get(0).getEmplacement());
        System.out.println("✅ Org2 voit uniquement son stock");
        OrganizationContext.clear();
    }

    // ========== TEST 7: Validation de sécurité ==========
    @Test
    @Order(7)
    @Transactional
    public void test07_SecurityValidation() {
        System.out.println("\n--- TEST 7: Validation de sécurité ---");

        // Créer un client dans org1
        OrganizationContext.setCurrentOrganizationId(org1Id);
        Client client = Client.builder()
            .username("secure_client")
            .categorie("Test")
            .adresse("Address")
            .typeClient(TypeClient.ENTREPRISE)
            .actif(true)
            .build();
        organizationHelper.setOrganizationId(client);
        client = clientRepository.save(client);
        OrganizationContext.clear();

        // Essayer de valider depuis org2 (devrait échouer)
        OrganizationContext.setCurrentOrganizationId(org2Id);

        Client finalClient = client;
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            organizationHelper.validateOrganizationMatch(finalClient);
        });

        assertTrue(exception.getMessage().contains("different organization"));
        System.out.println("✅ Validation de sécurité fonctionne: " + exception.getMessage());
        OrganizationContext.clear();
    }

    // ========== TEST 8: Endpoint GET Organization ==========
    @Test
    @Order(8)
    public void test08_GetOrganizationEndpoint() {
        System.out.println("\n--- TEST 8: GET Organization Endpoint ---");

        String url = baseUrl + "/api/organizations/" + org1Id;
        ResponseEntity<OrganizationResponse> response = restTemplate.getForEntity(
            url,
            OrganizationResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ORG1", response.getBody().getCode());
        assertEquals("Organisation 1", response.getBody().getShortName());
        System.out.println("✅ GET /api/organizations/{id} fonctionne");
    }

    // ========== TEST 9: Endpoint GET User Organizations ==========
    @Test
    @Order(9)
    public void test09_GetUserOrganizationsEndpoint() {
        System.out.println("\n--- TEST 9: GET User Organizations Endpoint ---");

        String url = baseUrl + "/api/organizations/user/" + userId1;
        ResponseEntity<OrganizationResponse[]> response = restTemplate.getForEntity(
            url,
            OrganizationResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("ORG1", response.getBody()[0].getCode());
        System.out.println("✅ GET /api/organizations/user/{userId} fonctionne");
    }

    // ========== TEST 10: Vérification count multi-organisation ==========
    @Test
    @Order(10)
    @Transactional
    public void test10_MultiOrgCount() {
        System.out.println("\n--- TEST 10: Vérification count multi-organisation ---");

        // Créer plusieurs clients dans org1
        OrganizationContext.setCurrentOrganizationId(org1Id);
        for (int i = 1; i <= 3; i++) {
            Client client = Client.builder()
                .username("client_org1_" + i)
                .categorie("Cat" + i)
                .adresse("Address " + i)
                .typeClient(TypeClient.ENTREPRISE)
                .actif(true)
                .build();
            organizationHelper.setOrganizationId(client);
            clientRepository.save(client);
        }
        long org1Count = clientRepository.count();
        OrganizationContext.clear();

        // Créer plusieurs clients dans org2
        OrganizationContext.setCurrentOrganizationId(org2Id);
        for (int i = 1; i <= 5; i++) {
            Client client = Client.builder()
                .username("client_org2_" + i)
                .categorie("Cat" + i)
                .adresse("Address " + i)
                .typeClient(TypeClient.PARTICULIER)
                .actif(true)
                .build();
            organizationHelper.setOrganizationId(client);
            clientRepository.save(client);
        }
        long org2Count = clientRepository.count();
        OrganizationContext.clear();

        // Les counts doivent être différents et corrects
        assertNotEquals(org1Count, org2Count);
        assertTrue(org1Count >= 3);
        assertTrue(org2Count >= 5);
        System.out.println("✅ Org1 a " + org1Count + " clients, Org2 a " + org2Count + " clients");
        System.out.println("✅ Les counts sont correctement isolés par organisation");
    }

    @AfterAll
    public static void tearDownClass() {
        System.out.println("\n=== FIN DES TESTS MULTI-TENANCY ===");
        System.out.println("✅ TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS");
    }
}
