package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FactureRepository extends JpaRepository<Facture, UUID> {
    Page<Facture> findAll(Pageable pageable);

    Optional<Facture> findByNumeroFacture(String numeroFacture);

    List<Facture> findByIdClient(UUID idClient);

    List<Facture> findByEtat(StatutFacture etat);

    List<Facture> findByType(String type);

    @Query("SELECT f FROM Facture f WHERE f.idClient = ?1 AND f.etat = ?2")
    List<Facture> findByClientAndEtat(UUID idClient, StatutFacture etat);

    @Query("SELECT f FROM Facture f WHERE f.dateFacturation BETWEEN ?1 AND ?2")
    List<Facture> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT f FROM Facture f WHERE f.dateEcheance BETWEEN ?1 AND ?2")
    List<Facture> findByDateEcheanceBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT f FROM Facture f WHERE f.dateEcheance < ?1 AND (f.etat = ENVOYE OR f.etat = PARTIELLEMENT_PAYE)")
    List<Facture> findOverdueFactures(LocalDate currentDate);

    @Query("SELECT f FROM Facture f WHERE f.montantTotal BETWEEN ?1 AND ?2")
    List<Facture> findByMontantTotalBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT f FROM Facture f WHERE f.montantRestant > 0")
    List<Facture> findUnpaidFactures();

    @Query("SELECT f FROM Facture f WHERE f.devise = ?1")
    List<Facture> findByDevise(String devise);

    @Query("SELECT f FROM Facture f WHERE f.envoyeParEmail = ?1")
    List<Facture> findByEnvoyeParEmail(Boolean envoyeParEmail);

    // Requêtes pour les statistiques
    @Query("SELECT COUNT(f) FROM Facture f WHERE f.etat = ?1")
    Long countByEtat(StatutFacture etat);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.idClient = ?1")
    Long countByIdClient(UUID idClient);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.dateFacturation BETWEEN ?1 AND ?2")
    Long countByDateFacturationBetween(LocalDate startDate, LocalDate endDate);

    // Requêtes pour sommes
    @Query("SELECT SUM(f.montantTotal) FROM Facture f WHERE f.dateFacturation BETWEEN ?1 AND ?2")
    BigDecimal sumMontantByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(f.montantTotal) FROM Facture f WHERE f.etat = ?1")
    BigDecimal sumMontantByEtat(StatutFacture etat);

    // Comptage par statut (String pour compatibilité)
    @Query("SELECT COUNT(f) FROM Facture f WHERE CAST(f.etat AS string) = ?1")
    Long countByStatut(String statut);

    @Query("SELECT SUM(f.montantTotal) FROM Facture f WHERE CAST(f.etat AS string) = ?1")
    BigDecimal sumMontantByStatut(String statut);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.dateFacturation >= ?1 AND f.dateFacturation <= ?2")
    Long countByDateBetween(LocalDate startDate, LocalDate endDate);

    // Requêtes avec pagination
    Slice<Facture> findByIdClient(UUID idClient, Pageable pageable);

    Slice<Facture> findByEtat(StatutFacture etat, Pageable pageable);

    @Query("SELECT f FROM Facture f WHERE f.dateFacturation BETWEEN ?1 AND ?2")
    Slice<Facture> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByNumeroFacture(String numeroFacture);
}
