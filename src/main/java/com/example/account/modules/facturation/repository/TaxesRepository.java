package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Taxes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface TaxesRepository extends R2dbcRepository<Taxes, UUID> {
    Flux<Taxes> findAllBy(Pageable pageable);

    Mono<Taxes> findByNomTaxe(String nomTaxe);

    Flux<Taxes> findByTypeTaxe(String typeTaxe);

    Flux<Taxes> findByActif(Boolean actif);

    Flux<Taxes> findByPorteTaxe(String porteTaxe);

    Flux<Taxes> findByPositionFiscale(String positionFiscale);

    @Query("SELECT * FROM taxes WHERE actif = true")
    Flux<Taxes> findAllActiveTaxes();

    @Query("SELECT * FROM taxes WHERE type_taxe = :typeTaxe AND actif = true")
    Flux<Taxes> findActiveByTypeTaxe(String typeTaxe);

    @Query("SELECT * FROM taxes WHERE calcul_taxe BETWEEN :minTaux AND :maxTaux")
    Flux<Taxes> findByCalculTaxeBetween(BigDecimal minTaux, BigDecimal maxTaux);

    @Query("SELECT * FROM taxes WHERE montant BETWEEN :minMontant AND :maxMontant")
    Flux<Taxes> findByMontantBetween(BigDecimal minMontant, BigDecimal maxMontant);

    @Query("SELECT COUNT(*) FROM taxes WHERE actif = true")
    Mono<Long> countActiveTaxes();

    @Query("SELECT COUNT(*) FROM taxes WHERE type_taxe = :typeTaxe")
    Mono<Long> countByTypeTaxe(String typeTaxe);

    Mono<Boolean> existsByNomTaxe(String nomTaxe);
}
