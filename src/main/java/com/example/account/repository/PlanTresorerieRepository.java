package com.example.account.repository;

import com.example.account.model.entity.PlanTresorerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanTresorerieRepository extends JpaRepository<PlanTresorerie, UUID> {

    Optional<PlanTresorerie> findByAnneeAndMois(Integer annee, Integer mois);

    List<PlanTresorerie> findByAnnee(Integer annee);

    List<PlanTresorerie> findByAnneeOrderByMoisAsc(Integer annee);

    @Query("SELECT p FROM PlanTresorerie p WHERE p.annee = ?1 AND p.mois BETWEEN ?2 AND ?3 ORDER BY p.mois")
    List<PlanTresorerie> findByAnneeAndMoisBetween(Integer annee, Integer moisDebut, Integer moisFin);

    @Query("SELECT p FROM PlanTresorerie p WHERE p.cloture = false ORDER BY p.annee, p.mois")
    List<PlanTresorerie> findPeriodesNonCloturees();

    @Query("SELECT p FROM PlanTresorerie p ORDER BY p.annee DESC, p.mois DESC")
    List<PlanTresorerie> findAllOrderByPeriodeDesc();
}
