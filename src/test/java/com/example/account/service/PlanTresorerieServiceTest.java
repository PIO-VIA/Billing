package com.example.account.service;

import com.example.account.model.entity.PlanTresorerie;
import com.example.account.repository.PlanTresorerieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanTresorerieServiceTest {

    @Mock
    private PlanTresorerieRepository planTresorerieRepository;

    @InjectMocks
    private PlanTresorerieService planTresorerieService;

    private PlanTresorerie planTresorerie;
    private UUID tresorerieId;

    @BeforeEach
    void setUp() {
        tresorerieId = UUID.randomUUID();

        planTresorerie = PlanTresorerie.builder()
                .idTresorerie(tresorerieId)
                .annee(2025)
                .mois(10)
                .periode("2025-10")
                .dateDebut(LocalDate.of(2025, 10, 1))
                .dateFin(LocalDate.of(2025, 10, 31))
                .soldeInitial(new BigDecimal("50000.00"))
                .encaissementsClients(new BigDecimal("30000.00"))
                .autresEncaissements(new BigDecimal("5000.00"))
                .decaissementsFournisseurs(new BigDecimal("20000.00"))
                .salaires(new BigDecimal("15000.00"))
                .chargesSociales(new BigDecimal("5000.00"))
                .impotsTaxes(new BigDecimal("3000.00"))
                .autresDecaissements(new BigDecimal("2000.00"))
                .devise("EUR")
                .statut("PREVISIONNEL")
                .cloture(false)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getPlanTresorerieById_shouldReturnPlanTresorerie() {
        // Given
        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.of(planTresorerie));

        // When
        PlanTresorerie result = planTresorerieService.getPlanTresorerieById(tresorerieId);

        // Then
        assertNotNull(result);
        assertEquals(tresorerieId, result.getIdTresorerie());
        assertEquals(2025, result.getAnnee());
        assertEquals(10, result.getMois());
        assertEquals("2025-10", result.getPeriode());
        assertEquals(new BigDecimal("50000.00"), result.getSoldeInitial());
        assertEquals("PREVISIONNEL", result.getStatut());
        assertFalse(result.getCloture());
        verify(planTresorerieRepository).findById(tresorerieId);
    }

    @Test
    void getPlanTresorerieById_shouldThrowException_whenNotFound() {
        // Given
        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> planTresorerieService.getPlanTresorerieById(tresorerieId));

        assertEquals("Plan de trésorerie non trouvé: " + tresorerieId, exception.getMessage());
        verify(planTresorerieRepository).findById(tresorerieId);
    }

    @Test
    void getPlanTresorerieByPeriode_shouldReturnPlanTresorerie() {
        // Given
        Integer annee = 2025;
        Integer mois = 10;
        when(planTresorerieRepository.findByAnneeAndMois(annee, mois)).thenReturn(Optional.of(planTresorerie));

        // When
        PlanTresorerie result = planTresorerieService.getPlanTresorerieByPeriode(annee, mois);

        // Then
        assertNotNull(result);
        assertEquals(annee, result.getAnnee());
        assertEquals(mois, result.getMois());
        verify(planTresorerieRepository).findByAnneeAndMois(annee, mois);
    }

    @Test
    void getPlanTresorerieByPeriode_shouldThrowException_whenNotFound() {
        // Given
        Integer annee = 2025;
        Integer mois = 11;
        when(planTresorerieRepository.findByAnneeAndMois(annee, mois)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> planTresorerieService.getPlanTresorerieByPeriode(annee, mois));

        assertEquals("Plan de trésorerie non trouvé pour 11/2025", exception.getMessage());
        verify(planTresorerieRepository).findByAnneeAndMois(annee, mois);
    }

    @Test
    void getPlansByAnnee_shouldReturnPlanList() {
        // Given
        Integer annee = 2025;
        PlanTresorerie plan2 = PlanTresorerie.builder()
                .idTresorerie(UUID.randomUUID())
                .annee(2025)
                .mois(11)
                .build();

        List<PlanTresorerie> plans = Arrays.asList(planTresorerie, plan2);
        when(planTresorerieRepository.findByAnneeOrderByMoisAsc(annee)).thenReturn(plans);

        // When
        List<PlanTresorerie> result = planTresorerieService.getPlansByAnnee(annee);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(p -> assertEquals(annee, p.getAnnee()));
        verify(planTresorerieRepository).findByAnneeOrderByMoisAsc(annee);
    }

    @Test
    void getPlansByPeriode_shouldReturnPlanList() {
        // Given
        Integer annee = 2025;
        Integer moisDebut = 10;
        Integer moisFin = 12;
        List<PlanTresorerie> plans = Arrays.asList(planTresorerie);
        when(planTresorerieRepository.findByAnneeAndMoisBetween(annee, moisDebut, moisFin)).thenReturn(plans);

        // When
        List<PlanTresorerie> result = planTresorerieService.getPlansByPeriode(annee, moisDebut, moisFin);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(planTresorerieRepository).findByAnneeAndMoisBetween(annee, moisDebut, moisFin);
    }

    @Test
    void getPeriodesNonCloturees_shouldReturnPlanList() {
        // Given
        List<PlanTresorerie> plans = Arrays.asList(planTresorerie);
        when(planTresorerieRepository.findPeriodesNonCloturees()).thenReturn(plans);

        // When
        List<PlanTresorerie> result = planTresorerieService.getPeriodesNonCloturees();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        result.forEach(p -> assertFalse(p.getCloture()));
        verify(planTresorerieRepository).findPeriodesNonCloturees();
    }

    @Test
    void getAllPlansOrderByPeriodeDesc_shouldReturnPlanList() {
        // Given
        List<PlanTresorerie> plans = Arrays.asList(planTresorerie);
        when(planTresorerieRepository.findAllOrderByPeriodeDesc()).thenReturn(plans);

        // When
        List<PlanTresorerie> result = planTresorerieService.getAllPlansOrderByPeriodeDesc();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(planTresorerieRepository).findAllOrderByPeriodeDesc();
    }

    @Test
    void calculerSoldesPrevisionnels_shouldCalculateCorrectly() {
        // Given
        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.of(planTresorerie));
        when(planTresorerieRepository.save(any(PlanTresorerie.class))).thenReturn(planTresorerie);

        // When
        PlanTresorerie result = planTresorerieService.calculerSoldesPrevisionnels(tresorerieId);

        // Then
        assertNotNull(result);

        // Vérifier les encaissements prévus: 30000 + 5000 = 35000
        assertEquals(new BigDecimal("35000.00"), result.getEncaissementsPrevus());

        // Vérifier les décaissements prévus: 20000 + 15000 + 5000 + 3000 + 2000 = 45000
        assertEquals(new BigDecimal("45000.00"), result.getDecaissementsPrevus());

        // Vérifier le solde final prévu: 50000 + 35000 - 45000 = 40000
        assertEquals(new BigDecimal("40000.00"), result.getSoldeFinalPrevu());

        assertNotNull(result.getUpdatedAt());
        verify(planTresorerieRepository).findById(tresorerieId);
        verify(planTresorerieRepository).save(planTresorerie);
    }

    @Test
    void calculerSoldesReels_shouldCalculateCorrectly() {
        // Given
        planTresorerie.setSoldeFinalPrevu(new BigDecimal("40000.00"));
        planTresorerie.setEncaissementsReels(new BigDecimal("33000.00"));
        planTresorerie.setDecaissementsReels(new BigDecimal("42000.00"));

        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.of(planTresorerie));
        when(planTresorerieRepository.save(any(PlanTresorerie.class))).thenReturn(planTresorerie);

        // When
        PlanTresorerie result = planTresorerieService.calculerSoldesReels(tresorerieId);

        // Then
        assertNotNull(result);

        // Vérifier le solde final réel: 50000 + 33000 - 42000 = 41000
        assertEquals(new BigDecimal("41000.00"), result.getSoldeFinalReel());

        // Vérifier l'écart: 41000 - 40000 = 1000
        assertEquals(new BigDecimal("1000.00"), result.getEcart());

        assertEquals("REALISE", result.getStatut());
        assertNotNull(result.getUpdatedAt());
        verify(planTresorerieRepository).findById(tresorerieId);
        verify(planTresorerieRepository).save(planTresorerie);
    }

    @Test
    void calculerSoldesReels_shouldHandleNullSoldeFinalPrevu() {
        // Given
        planTresorerie.setSoldeFinalPrevu(null);
        planTresorerie.setEncaissementsReels(new BigDecimal("33000.00"));
        planTresorerie.setDecaissementsReels(new BigDecimal("42000.00"));

        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.of(planTresorerie));
        when(planTresorerieRepository.save(any(PlanTresorerie.class))).thenReturn(planTresorerie);

        // When
        PlanTresorerie result = planTresorerieService.calculerSoldesReels(tresorerieId);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("41000.00"), result.getSoldeFinalReel());
        assertEquals(new BigDecimal("41000.00"), result.getEcart());
        verify(planTresorerieRepository).save(planTresorerie);
    }

    @Test
    void cloturerPeriode_shouldClosePeriod() {
        // Given
        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.of(planTresorerie));
        when(planTresorerieRepository.save(any(PlanTresorerie.class))).thenReturn(planTresorerie);

        // When
        PlanTresorerie result = planTresorerieService.cloturerPeriode(tresorerieId);

        // Then
        assertNotNull(result);
        assertTrue(result.getCloture());
        assertNotNull(result.getDateCloture());
        assertNotNull(result.getUpdatedAt());
        verify(planTresorerieRepository).findById(tresorerieId);
        verify(planTresorerieRepository).save(planTresorerie);
    }

    @Test
    void cloturerPeriode_shouldThrowException_whenAlreadyClosed() {
        // Given
        planTresorerie.setCloture(true);
        planTresorerie.setDateCloture(LocalDateTime.now());
        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.of(planTresorerie));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> planTresorerieService.cloturerPeriode(tresorerieId));

        assertEquals("La période est déjà clôturée", exception.getMessage());
        verify(planTresorerieRepository).findById(tresorerieId);
        verify(planTresorerieRepository, never()).save(any(PlanTresorerie.class));
    }

    @Test
    void calculerSoldesPrevisionnels_shouldThrowException_whenPlanNotFound() {
        // Given
        when(planTresorerieRepository.findById(tresorerieId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> planTresorerieService.calculerSoldesPrevisionnels(tresorerieId));

        assertEquals("Plan de trésorerie non trouvé: " + tresorerieId, exception.getMessage());
        verify(planTresorerieRepository).findById(tresorerieId);
        verify(planTresorerieRepository, never()).save(any(PlanTresorerie.class));
    }
}
