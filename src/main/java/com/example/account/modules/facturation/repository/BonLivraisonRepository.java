package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonLivraison;
import com.example.account.modules.facturation.model.enums.StatutBonLivraison;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface BonLivraisonRepository extends R2dbcRepository<BonLivraison, UUID> {

    Flux<BonLivraison> findAllBy(Pageable pageable);

    Mono<BonLivraison> findByNumeroBonLivraison(String numeroBonLivraison);

    Flux<BonLivraison> findByIdClient(UUID idClient);

    Flux<BonLivraison> findByIdFacture(UUID idFacture);

    Flux<BonLivraison> findByStatut(StatutBonLivraison statut);

    @Query("SELECT * FROM bons_livraison bl WHERE bl.date_livraison BETWEEN :startDate AND :endDate")
    Flux<BonLivraison> findByDateLivraisonBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT * FROM bons_livraison bl WHERE bl.livraison_effectuee = false AND bl.date_livraison < :dateReference")
    Flux<BonLivraison> findLivraisonsEnRetard(@Param("dateReference") LocalDate dateReference);

    Mono<Boolean> existsByNumeroBonLivraison(String numeroBonLivraison);
}
