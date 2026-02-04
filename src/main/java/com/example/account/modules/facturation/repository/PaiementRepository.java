package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Paiement;
import com.example.account.modules.facturation.model.enums.TypePaiement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface PaiementRepository extends R2dbcRepository<Paiement, UUID> {
    Flux<Paiement> findAllBy(Pageable pageable);

    Flux<Paiement> findByIdClient(UUID idClient);

    Flux<Paiement> findByIdFacture(UUID idFacture);

    Flux<Paiement> findByModePaiement(TypePaiement modePaiement);

    Flux<Paiement> findByJournal(String journal);

    @Query("SELECT * FROM paiements p WHERE p.date BETWEEN :startDate AND :endDate")
    Flux<Paiement> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM paiements p WHERE p.montant BETWEEN :minAmount AND :maxAmount")
    Flux<Paiement> findByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT * FROM paiements p WHERE p.id_client = :idClient AND p.date BETWEEN :startDate AND :endDate")
    Flux<Paiement> findByClientAndDateBetween(UUID idClient, LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM paiements p WHERE p.id_facture = :idFacture ORDER BY p.date DESC")
    Flux<Paiement> findByFactureOrderByDateDesc(UUID idFacture);

    @Query("SELECT * FROM paiements p WHERE p.mode_paiement = :modePaiement AND p.date BETWEEN :startDate AND :endDate")
    Flux<Paiement> findByModePaiementAndDateBetween(TypePaiement modePaiement, LocalDate startDate, LocalDate endDate);

    // Requêtes d'agrégation
    @Query("SELECT SUM(p.montant) FROM paiements p WHERE p.id_client = :idClient")
    Mono<BigDecimal> sumMontantByClient(UUID idClient);

    @Query("SELECT SUM(p.montant) FROM paiements p WHERE p.id_facture = :idFacture")
    Mono<BigDecimal> sumMontantByFacture(UUID idFacture);

    @Query("SELECT SUM(p.montant) FROM paiements p WHERE p.date BETWEEN :startDate AND :endDate")
    Mono<BigDecimal> sumMontantByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(*) FROM paiements p WHERE p.id_client = :idClient")
    Mono<Long> countByIdClient(UUID idClient);

    @Query("SELECT COUNT(*) FROM paiements p WHERE p.mode_paiement = :modePaiement")
    Mono<Long> countByModePaiement(TypePaiement modePaiement);

    @Query("SELECT COUNT(*) FROM paiements p WHERE p.date BETWEEN :startDate AND :endDate")
    Mono<Long> countByDateBetween(LocalDate startDate, LocalDate endDate);
}