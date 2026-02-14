package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Paiement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PaiementRepository extends R2dbcRepository<Paiement, UUID> {
    Flux<Paiement> findByIdClient(UUID idClient);
    Flux<Paiement> findByIdFacture(UUID idFacture);
    Flux<Paiement> findByModePaiement(com.example.account.modules.facturation.model.enums.TypePaiement mode);
    Flux<Paiement> findByDateBetween(java.time.LocalDate start, java.time.LocalDate end);

    @Query("SELECT SUM(montant) FROM paiements WHERE id_client = :idClient")
    Mono<java.math.BigDecimal> sumMontantByClient(UUID idClient);

    @Query("SELECT SUM(montant) FROM paiements WHERE id_facture = :idFacture")
    Mono<java.math.BigDecimal> sumMontantByFacture(UUID idFacture);

    @Query("SELECT SUM(montant) FROM paiements WHERE date BETWEEN :start AND :end")
    Mono<java.math.BigDecimal> sumMontantByDateBetween(java.time.LocalDate start, java.time.LocalDate end);

    Mono<Long> countByIdClient(UUID idClient);
    Mono<Long> countByModePaiement(com.example.account.modules.facturation.model.enums.TypePaiement mode);
}
