package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Reactive repository for Facture entity operations.
 */
@Repository
public interface FactureRepository extends R2dbcRepository<Facture, UUID> {
    
    Mono<Facture> findByNumeroFacture(String numeroFacture);
    
    Flux<Facture> findByIdClient(UUID idClient);
    
    Flux<Facture> findByEtat(StatutFacture etat);
    
    Flux<Facture> findByType(String type);
    
    @Query("SELECT * FROM factures WHERE id_client = :idClient AND etat = :etat")
    Flux<Facture> findByClientAndEtat(UUID idClient, StatutFacture etat);
    
    @Query("SELECT * FROM factures WHERE date_facturation BETWEEN :startDate AND :endDate")
    Flux<Facture> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT * FROM factures WHERE date_echeance BETWEEN :startDate AND :endDate")
    Flux<Facture> findByDateEcheanceBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT * FROM factures WHERE date_echeance < :currentDate AND (etat = 'ENVOYE' OR etat = 'PARTIELLEMENT_PAYE')")
    Flux<Facture> findOverdueFactures(LocalDate currentDate);
    
    @Query("SELECT * FROM factures WHERE montant_total BETWEEN :minAmount AND :maxAmount")
    Flux<Facture> findByMontantTotalBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    @Query("SELECT * FROM factures WHERE montant_restant > 0")
    Flux<Facture> findUnpaidFactures();
    
    @Query("SELECT * FROM factures WHERE devise = :devise")
    Flux<Facture> findByDevise(String devise);
    
    @Query("SELECT * FROM factures WHERE envoye_par_email = :envoyeParEmail")
    Flux<Facture> findByEnvoyeParEmail(Boolean envoyeParEmail);
    
    @Query("SELECT COUNT(*) FROM factures WHERE etat = :etat")
    Mono<Long> countByEtat(StatutFacture etat);
    
    @Query("SELECT COUNT(*) FROM factures WHERE id_client = :idClient")
    Mono<Long> countByIdClient(UUID idClient);
    
    @Query("SELECT COUNT(*) FROM factures WHERE date_facturation BETWEEN :startDate AND :endDate")
    Mono<Long> countByDateFacturationBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(montant_total) FROM factures WHERE date_facturation BETWEEN :startDate AND :endDate")
    Mono<BigDecimal> sumMontantByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(montant_total) FROM factures WHERE etat = :etat")
    Mono<BigDecimal> sumMontantByEtat(StatutFacture etat);
    
    @Query("SELECT COUNT(*) FROM factures WHERE CAST(etat AS TEXT) = :statut")
    Mono<Long> countByStatut(String statut);
    
    @Query("SELECT SUM(montant_total) FROM factures WHERE CAST(etat AS TEXT) = :statut")
    Mono<BigDecimal> sumMontantByStatut(String statut);
    
    @Query("SELECT COUNT(*) FROM factures WHERE date_facturation >= :startDate AND date_facturation <= :endDate")
    Mono<Long> countByDateBetween(LocalDate startDate, LocalDate endDate);
    
    Mono<Boolean> existsByNumeroFacture(String numeroFacture);
}
