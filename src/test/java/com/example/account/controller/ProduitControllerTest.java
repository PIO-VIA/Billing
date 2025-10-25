package com.example.account.controller;

import com.example.account.dto.request.ProduitCreateRequest;
import com.example.account.dto.request.ProduitUpdateRequest;
import com.example.account.dto.response.ProduitResponse;
import com.example.account.service.ProduitService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProduitController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProduitService produitService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProduitResponse produitResponse;
    private ProduitCreateRequest produitCreateRequest;
    private ProduitUpdateRequest produitUpdateRequest;
    private UUID produitId;

    @BeforeEach
    void setUp() {
        produitId = UUID.randomUUID();

        produitResponse = ProduitResponse.builder()
                .idProduit(produitId)
                .nomProduit("Test Produit")
                .typeProduit("Service")
                .prixVente(new BigDecimal("100.00"))
                .cout(new BigDecimal("50.00"))
                .categorie("Technologie")
                .reference("REF001")
                .codeBarre("123456789")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        produitCreateRequest = ProduitCreateRequest.builder()
                .nomProduit("Test Produit")
                .typeProduit("Service")
                .prixVente(new BigDecimal("100.00"))
                .cout(new BigDecimal("50.00"))
                .categorie("Technologie")
                .reference("REF001")
                .codeBarre("123456789")
                .active(true)
                .build();

        produitUpdateRequest = ProduitUpdateRequest.builder()
                .prixVente(new BigDecimal("120.00"))
                .build();
    }

    @Test
    void createProduit_shouldReturnCreatedProduit() throws Exception {
        when(produitService.createProduit(any(ProduitCreateRequest.class))).thenReturn(produitResponse);

        mockMvc.perform(post("/api/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produitCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProduit").value(produitId.toString()))
                .andExpect(jsonPath("$.nomProduit").value("Test Produit"))
                .andExpect(jsonPath("$.prixVente").value(100.00));
    }

    @Test
    void updateProduit_shouldReturnUpdatedProduit() throws Exception {
        when(produitService.updateProduit(eq(produitId), any(ProduitUpdateRequest.class))).thenReturn(produitResponse);

        mockMvc.perform(put("/api/produits/{produitId}", produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produitUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduit").value(produitId.toString()));
    }

    @Test
    void getProduitById_shouldReturnProduit() throws Exception {
        when(produitService.getProduitById(produitId)).thenReturn(produitResponse);

        mockMvc.perform(get("/api/produits/{produitId}", produitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduit").value(produitId.toString()))
                .andExpect(jsonPath("$.nomProduit").value("Test Produit"));
    }

    @Test
    void getProduitByReference_shouldReturnProduit() throws Exception {
        when(produitService.getProduitByReference("REF001")).thenReturn(produitResponse);

        mockMvc.perform(get("/api/produits/reference/{reference}", "REF001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("REF001"));
    }

    @Test
    void getProduitByCodeBarre_shouldReturnProduit() throws Exception {
        when(produitService.getProduitByCodeBarre("123456789")).thenReturn(produitResponse);

        mockMvc.perform(get("/api/produits/code-barre/{codeBarre}", "123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeBarre").value("123456789"));
    }

    @Test
    void getAllProduits_shouldReturnProduitList() throws Exception {
        List<ProduitResponse> produits = Collections.singletonList(produitResponse);
        when(produitService.getAllProduits()).thenReturn(produits);

        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProduit").value(produitId.toString()));
    }

    @Test
    void getAllProduitsPaginated_shouldReturnPaginatedProduitList() throws Exception {
        Page<ProduitResponse> produits = new PageImpl<>(Collections.singletonList(produitResponse));
        when(produitService.getAllProduits(any(Pageable.class))).thenReturn(produits);

        mockMvc.perform(get("/api/produits/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idProduit").value(produitId.toString()));
    }

    @Test
    void getActiveProduits_shouldReturnActiveProduitList() throws Exception {
        List<ProduitResponse> activeProduits = Collections.singletonList(produitResponse);
        when(produitService.getActiveProduits()).thenReturn(activeProduits);

        mockMvc.perform(get("/api/produits/actifs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProduit").value(produitId.toString()));
    }

    @Test
    void getProduitsByCategorie_shouldReturnProduitList() throws Exception {
        List<ProduitResponse> produits = Collections.singletonList(produitResponse);
        when(produitService.getProduitsByCategorie("Technologie")).thenReturn(produits);

        mockMvc.perform(get("/api/produits/categorie/{categorie}", "Technologie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categorie").value("Technologie"));
    }

    @Test
    void getProduitsByType_shouldReturnProduitList() throws Exception {
        List<ProduitResponse> produits = Collections.singletonList(produitResponse);
        when(produitService.getProduitsByType("Service")).thenReturn(produits);

        mockMvc.perform(get("/api/produits/type/{typeProduit}", "Service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeProduit").value("Service"));
    }

    @Test
    void searchProduitsByNom_shouldReturnProduitList() throws Exception {
        List<ProduitResponse> produits = Collections.singletonList(produitResponse);
        when(produitService.searchProduitsByNom("Test")).thenReturn(produits);

        mockMvc.perform(get("/api/produits/search")
                        .param("nom", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomProduit").value("Test Produit"));
    }

    @Test
    void getProduitsByPriceRange_shouldReturnProduitList() throws Exception {
        List<ProduitResponse> produits = Collections.singletonList(produitResponse);
        when(produitService.getProduitsByPriceRange(new BigDecimal("50.00"), new BigDecimal("150.00")))
                .thenReturn(produits);

        mockMvc.perform(get("/api/produits/prix-range")
                        .param("minPrice", "50.00")
                        .param("maxPrice", "150.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProduit").value(produitId.toString()));
    }

    @Test
    void deleteProduit_shouldReturnNoContent() throws Exception {
        doNothing().when(produitService).deleteProduit(produitId);

        mockMvc.perform(delete("/api/produits/{produitId}", produitId))
                .andExpect(status().isNoContent());
    }

    @Test
    void desactiverProduit_shouldReturnUpdatedProduit() throws Exception {
        when(produitService.desactiverProduit(produitId)).thenReturn(produitResponse);

        mockMvc.perform(put("/api/produits/{produitId}/desactiver", produitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduit").value(produitId.toString()));
    }

    @Test
    void activerProduit_shouldReturnUpdatedProduit() throws Exception {
        when(produitService.activerProduit(produitId)).thenReturn(produitResponse);

        mockMvc.perform(put("/api/produits/{produitId}/activer", produitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduit").value(produitId.toString()));
    }

    @Test
    void countByCategorie_shouldReturnCount() throws Exception {
        when(produitService.countByCategorie("Technologie")).thenReturn(5L);

        mockMvc.perform(get("/api/produits/count/categorie/{categorie}", "Technologie"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void countByType_shouldReturnCount() throws Exception {
        when(produitService.countByType("Service")).thenReturn(3L);

        mockMvc.perform(get("/api/produits/count/type/{typeProduit}", "Service"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
