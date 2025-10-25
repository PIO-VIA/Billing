package com.example.account.controller;

import com.example.account.dto.request.JournalCreateRequest;
import com.example.account.dto.request.JournalUpdateRequest;
import com.example.account.dto.response.JournalResponse;
import com.example.account.service.JournalService;
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

@WebMvcTest(JournalController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class JournalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JournalService journalService;

    @Autowired
    private ObjectMapper objectMapper;

    private JournalResponse journalResponse;
    private JournalCreateRequest journalCreateRequest;
    private JournalUpdateRequest journalUpdateRequest;
    private UUID journalId;

    @BeforeEach
    void setUp() {
        journalId = UUID.randomUUID();

        journalResponse = JournalResponse.builder()
                .idJournal(journalId)
                .nomJournal("Journal Ventes")
                .type("Vente")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        journalCreateRequest = JournalCreateRequest.builder()
                .nomJournal("Journal Ventes")
                .type("Vente")
                .build();

        journalUpdateRequest = JournalUpdateRequest.builder()
                .nomJournal("Journal Ventes Modifié")
                .type("Vente")
                .build();
    }

    @Test
    void createJournal_shouldReturnCreatedJournal() throws Exception {
        when(journalService.createJournal(any(JournalCreateRequest.class))).thenReturn(journalResponse);

        mockMvc.perform(post("/api/journals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idJournal").value(journalId.toString()))
                .andExpect(jsonPath("$.nomJournal").value("Journal Ventes"))
                .andExpect(jsonPath("$.type").value("Vente"));
    }

    @Test
    void updateJournal_shouldReturnUpdatedJournal() throws Exception {
        when(journalService.updateJournal(eq(journalId), any(JournalUpdateRequest.class))).thenReturn(journalResponse);

        mockMvc.perform(put("/api/journals/{journalId}", journalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idJournal").value(journalId.toString()));
    }

    @Test
    void getJournalById_shouldReturnJournal() throws Exception {
        when(journalService.getJournalById(journalId)).thenReturn(journalResponse);

        mockMvc.perform(get("/api/journals/{journalId}", journalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idJournal").value(journalId.toString()))
                .andExpect(jsonPath("$.nomJournal").value("Journal Ventes"));
    }

    @Test
    void getJournalByNom_shouldReturnJournal() throws Exception {
        when(journalService.getJournalByNom("Journal Ventes")).thenReturn(journalResponse);

        mockMvc.perform(get("/api/journals/nom/{nomJournal}", "Journal Ventes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomJournal").value("Journal Ventes"));
    }

    @Test
    void getAllJournals_shouldReturnJournalList() throws Exception {
        List<JournalResponse> journals = Collections.singletonList(journalResponse);
        when(journalService.getAllJournals()).thenReturn(journals);

        mockMvc.perform(get("/api/journals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idJournal").value(journalId.toString()));
    }

    @Test
    void getAllJournalsPaginated_shouldReturnPaginatedJournalList() throws Exception {
        Page<JournalResponse> journals = new PageImpl<>(Collections.singletonList(journalResponse));
        when(journalService.getAllJournals(any(Pageable.class))).thenReturn(journals);

        mockMvc.perform(get("/api/journals/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idJournal").value(journalId.toString()));
    }

    @Test
    void getJournalsByType_shouldReturnJournalList() throws Exception {
        List<JournalResponse> journals = Collections.singletonList(journalResponse);
        when(journalService.getJournalsByType("Vente")).thenReturn(journals);

        mockMvc.perform(get("/api/journals/type/{type}", "Vente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("Vente"));
    }

    @Test
    void searchJournalsByNom_shouldReturnJournalList() throws Exception {
        List<JournalResponse> journals = Collections.singletonList(journalResponse);
        when(journalService.searchJournalsByNom("Ventes")).thenReturn(journals);

        mockMvc.perform(get("/api/journals/search")
                        .param("nom", "Ventes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomJournal").value("Journal Ventes"));
    }

    @Test
    void deleteJournal_shouldReturnNoContent() throws Exception {
        doNothing().when(journalService).deleteJournal(journalId);

        mockMvc.perform(delete("/api/journals/{journalId}", journalId))
                .andExpect(status().isNoContent());
    }

    @Test
    void countByType_shouldReturnCount() throws Exception {
        when(journalService.countByType("Vente")).thenReturn(5L);

        mockMvc.perform(get("/api/journals/count/type/{type}", "Vente"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
