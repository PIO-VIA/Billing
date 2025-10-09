package com.example.account.repository;

import com.example.account.model.entity.Paiement;
import com.example.account.model.enums.TypePaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, UUID> {
    Page<Paiement> findAll(Pageable pageable);

    List<Paiement> findByIdClient(UUID idClient);

    List<Paiement> findByIdFacture(UUID idFacture);

    List<Paiement> findByModePaiement(TypePaiement modePaiement);

    List<Paiement> findByJournal(String journal);

    @Query("SELECT p FROM Paiement p WHERE p.date BETWEEN ?1 AND ?2")
    List<Paiement> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Paiement p WHERE p.montant BETWEEN ?1 AND ?2")
    List<Paiement> findByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT p FROM Paiement p WHERE p.idClient = ?1 AND p.date BETWEEN ?2 AND ?3")
    List<Paiement> findByClientAndDateBetween(UUID idClient, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Paiement p WHERE p.idFacture = ?1 ORDER BY p.date DESC")
    List<Paiement> findByFactureOrderByDateDesc(UUID idFacture);

    @Query("SELECT p FROM Paiement p WHERE p.modePaiement = ?1 AND p.date BETWEEN ?2 AND ?3")
    List<Paiement> findByModePaiementAndDateBetween(TypePaiement modePaiement, LocalDate startDate, LocalDate endDate);

    // Requêtes d'agrégation
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.idClient = ?1")
    BigDecimal sumMontantByClient(UUID idClient);

    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.idFacture = ?1")
    BigDecimal sumMontantByFacture(UUID idFacture);

    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.date BETWEEN ?1 AND ?2")
    BigDecimal sumMontantByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.idClient = ?1")
    Long countByIdClient(UUID idClient);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.modePaiement = ?1")
    Long countByModePaiement(TypePaiement modePaiement);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.date BETWEEN ?1 AND ?2")
    Long countByDateBetween(LocalDate startDate, LocalDate endDate);
}