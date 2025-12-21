package com.example.account;

import com.example.account.context.OrganizationContext;
import com.example.account.filter.OrganizationFilter;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour OrganizationFilter.
 *
 * Vérifie:
 * - Extraction correcte du header X-Organization-ID
 * - Validation du format UUID
 * - Gestion des erreurs
 * - Nettoyage du contexte
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrganizationFilterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private com.example.account.repository.UserOrganizationRepository userOrganizationRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Session hibernateSession;

    @Mock
    private org.hibernate.Filter hibernateFilter;

    private OrganizationFilter filter;

    @BeforeEach
    public void setup() {
        filter = new OrganizationFilter(entityManager, userOrganizationRepository);
    }

    @AfterEach
    public void cleanup() {
        OrganizationContext.clear();
    }

    @Test
    public void test_ValidOrganizationHeader() throws Exception {
        System.out.println("\n--- TEST: Header X-Organization-ID valide ---");

        UUID orgId = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/clients");
        when(request.getHeader("X-Organization-ID")).thenReturn(orgId.toString());
        when(entityManager.unwrap(Session.class)).thenReturn(hibernateSession);
        when(hibernateSession.enableFilter("organizationFilter")).thenReturn(hibernateFilter);
        when(hibernateFilter.setParameter(anyString(), any())).thenReturn(hibernateFilter);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(hibernateSession).enableFilter("organizationFilter");
        verify(hibernateFilter).setParameter("organizationId", orgId);

        System.out.println("✅ Header valide traité correctement");
    }

    @Test
    public void test_MissingOrganizationHeader() throws Exception {
        System.out.println("\n--- TEST: Header X-Organization-ID manquant ---");

        when(request.getRequestURI()).thenReturn("/api/clients");
        when(request.getHeader("X-Organization-ID")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(filterChain, never()).doFilter(request, response);

        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("Missing X-Organization-ID header"));

        System.out.println("✅ Requête sans header rejetée: " + responseBody);
    }

    @Test
    public void test_InvalidOrganizationHeaderFormat() throws Exception {
        System.out.println("\n--- TEST: Format X-Organization-ID invalide ---");

        when(request.getRequestURI()).thenReturn("/api/clients");
        when(request.getHeader("X-Organization-ID")).thenReturn("invalid-uuid-format");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(filterChain, never()).doFilter(request, response);

        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("Invalid X-Organization-ID format"));

        System.out.println("✅ UUID invalide rejeté: " + responseBody);
    }

    @Test
    public void test_ExcludedPaths() throws Exception {
        System.out.println("\n--- TEST: Chemins exclus ---");

        String[] excludedPaths = {
            "/api/auth/login",
            "/api/users/register",
            "/api/health",
            "/actuator/health",
            "/swagger-ui/index.html",
            "/api/organizations/create"
        };

        for (String path : excludedPaths) {
            when(request.getRequestURI()).thenReturn(path);

            filter.doFilter(request, response, filterChain);

            verify(filterChain, atLeastOnce()).doFilter(request, response);
            System.out.println("✅ Chemin exclu autorisé: " + path);
        }
    }

    @Test
    public void test_ContextCleanup() throws Exception {
        System.out.println("\n--- TEST: Nettoyage du contexte ---");

        UUID orgId = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/clients");
        when(request.getHeader("X-Organization-ID")).thenReturn(orgId.toString());
        when(entityManager.unwrap(Session.class)).thenReturn(hibernateSession);
        when(hibernateSession.enableFilter("organizationFilter")).thenReturn(hibernateFilter);
        when(hibernateFilter.setParameter(anyString(), any())).thenReturn(hibernateFilter);

        filter.doFilter(request, response, filterChain);

        // Vérifier que le contexte est nettoyé après le traitement
        assertNull(OrganizationContext.getCurrentOrganizationIdOrNull());
        verify(hibernateSession).disableFilter("organizationFilter");

        System.out.println("✅ Contexte nettoyé après traitement");
    }
}
