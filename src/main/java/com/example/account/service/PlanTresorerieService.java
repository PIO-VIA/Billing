package com.example.account.service;

import com.example.account.model.entity.PlanTresorerie;
import com.example.account.repository.PlanTresorerieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanTresorerieService {

    private final PlanTresorerieRepository planTresorerieRepository;

    @Transactional(readOnly = true)
    public PlanTresorerie getPlanTresorerieById(UUID tresorerieId) {
        log.info("Récupération du plan de trésorerie: {}", tresorerieId);
        return planTresorerieRepository.findById(tresorerieId)
                .orElseThrow(() -> new IllegalArgumentException("Plan de trésorerie non trouvé: " + tresorerieId));
    }

    @Transactional(readOnly = true)
    public PlanTresorerie getPlanTresorerieByPeriode(Integer annee, Integer mois) {
        log.info("Récupération du plan de trésorerie pour {}/{}", mois, annee);
        return planTresorerieRepository.findByAnneeAndMois(annee, mois)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Plan de trésorerie non trouvé pour %d/%d", mois, annee)));
    }

    @Transactional(readOnly = true)
    public List<PlanTresorerie> getPlansByAnnee(Integer annee) {
        log.info("Récupération des plans de trésorerie pour l'année: {}", annee);
        return planTresorerieRepository.findByAnneeOrderByMoisAsc(annee);
    }

    @Transactional(readOnly = true)
    public List<PlanTresorerie> getPlansByPeriode(Integer annee, Integer moisDebut, Integer moisFin) {
        log.info("Récupération des plans de trésorerie entre {}/{} et {}/{}", moisDebut, annee, moisFin, annee);
        return planTresorerieRepository.findByAnneeAndMoisBetween(annee, moisDebut, moisFin);
    }

    @Transactional(readOnly = true)
    public List<PlanTresorerie> getPeriodesNonCloturees() {
        log.info("Récupération des périodes non clôturées");
        return planTresorerieRepository.findPeriodesNonCloturees();
    }

    @Transactional(readOnly = true)
    public List<PlanTresorerie> getAllPlansOrderByPeriodeDesc() {
        log.info("Récupération de tous les plans de trésorerie");
        return planTresorerieRepository.findAllOrderByPeriodeDesc();
    }

    @Transactional
    public PlanTresorerie calculerSoldesPrevisionnels(UUID tresorerieId) {
        log.info("Calcul des soldes prévisionnels pour: {}", tresorerieId);
        PlanTresorerie plan = getPlanTresorerieById(tresorerieId);

        // Calcul des encaissements prévus totaux
        BigDecimal encaissementsTotaux = plan.getEncaissementsClients()
                .add(plan.getAutresEncaissements());

        // Calcul des décaissements prévus totaux
        BigDecimal decaissementsTotaux = plan.getDecaissementsFournisseurs()
                .add(plan.getSalaires())
                .add(plan.getChargesSociales())
                .add(plan.getImpotsTaxes())
                .add(plan.getAutresDecaissements());

        // Calcul du solde final prévu
        BigDecimal soldeFinalPrevu = plan.getSoldeInitial()
                .add(encaissementsTotaux)
                .subtract(decaissementsTotaux);

        plan.setEncaissementsPrevus(encaissementsTotaux);
        plan.setDecaissementsPrevus(decaissementsTotaux);
        plan.setSoldeFinalPrevu(soldeFinalPrevu);
        plan.setUpdatedAt(LocalDateTime.now());

        return planTresorerieRepository.save(plan);
    }

    @Transactional
    public PlanTresorerie calculerSoldesReels(UUID tresorerieId) {
        log.info("Calcul des soldes réels pour: {}", tresorerieId);
        PlanTresorerie plan = getPlanTresorerieById(tresorerieId);

        // Calcul du solde final réel
        BigDecimal soldeFinalReel = plan.getSoldeInitial()
                .add(plan.getEncaissementsReels())
                .subtract(plan.getDecaissementsReels());

        // Calcul de l'écart entre prévu et réel
        BigDecimal ecart = soldeFinalReel.subtract(plan.getSoldeFinalPrevu() != null
                ? plan.getSoldeFinalPrevu()
                : BigDecimal.ZERO);

        plan.setSoldeFinalReel(soldeFinalReel);
        plan.setEcart(ecart);
        plan.setStatut("REALISE");
        plan.setUpdatedAt(LocalDateTime.now());

        return planTresorerieRepository.save(plan);
    }

    @Transactional
    public PlanTresorerie cloturerPeriode(UUID tresorerieId) {
        log.info("Clôture du plan de trésorerie: {}", tresorerieId);
        PlanTresorerie plan = getPlanTresorerieById(tresorerieId);

        if (plan.getCloture()) {
            throw new IllegalStateException("La période est déjà clôturée");
        }

        plan.setCloture(true);
        plan.setDateCloture(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());

        return planTresorerieRepository.save(plan);
    }
}
