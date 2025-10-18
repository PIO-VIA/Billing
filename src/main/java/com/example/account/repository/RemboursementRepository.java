package com.example.account.repository;

import com.example.account.model.entity.Remboursement;
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
public interface RemboursementRepository extends JpaRepository<Remboursement, UUID> {
    Page<Remboursement> findAll(Pageable pageable);

    List<Remboursement> findByIdClient(UUID idClient);

    List<Remboursement> findByIdFacture(UUID idFacture);

    List<Remboursement> findByStatut(String statut);

    List<Remboursement> findByDevise(String devise);

    @Query("SELECT r FROM Remboursement r WHERE r.dateFacturation BETWEEN ?1 AND ?2")
    List<Remboursement> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Remboursement r WHERE r.dateEcheance BETWEEN ?1 AND ?2")
    List<Remboursement> findByDateEcheanceBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Remboursement r WHERE r.dateComptable BETWEEN ?1 AND ?2")
    List<Remboursement> findByDateComptableBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Remboursement r WHERE r.montant BETWEEN ?1 AND ?2")
    List<Remboursement> findByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT r FROM Remboursement r WHERE r.statut = ?1 AND r.dateEcheance < ?2")
    List<Remboursement> findOverdueRemboursements(String statut, LocalDate currentDate);

    @Query("SELECT r FROM Remboursement r WHERE r.idClient = ?1 AND r.statut = ?2")
    List<Remboursement> findByClientAndStatut(UUID idClient, String statut);

    @Query("SELECT SUM(r.montant) FROM Remboursement r WHERE r.idClient = ?1")
    BigDecimal sumMontantByClient(UUID idClient);

    @Query("SELECT SUM(r.montant) FROM Remboursement r WHERE r.statut = ?1")
    BigDecimal sumMontantByStatut(String statut);

    @Query("SELECT COUNT(r) FROM Remboursement r WHERE r.statut = ?1")
    Long countByStatut(String statut);

    @Query("SELECT COUNT(r) FROM Remboursement r WHERE r.idClient = ?1")
    Long countByIdClient(UUID idClient);
}
