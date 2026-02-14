package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Taxes;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import java.util.UUID;

@Repository
public interface TaxesRepository extends R2dbcRepository<Taxes, UUID> {
    Mono<Taxes> findByNomTaxe(String nomTaxe);
    Flux<Taxes> findByActifTrue();
    Flux<Taxes> findByTypeTaxe(String typeTaxe);
    Mono<Boolean> existsByNomTaxe(String nomTaxe);
    
    @Query("SELECT * FROM taxes WHERE actif = true")
    Flux<Taxes> findAllActiveTaxes();
    
    @Query("SELECT * FROM taxes WHERE type_taxe = :typeTaxe AND actif = true")
    Flux<Taxes> findActiveByTypeTaxe(String typeTaxe);
    
    Flux<Taxes> findByPorteTaxe(String porteTaxe);
    Flux<Taxes> findByPositionFiscale(String positionFiscale);
    Flux<Taxes> findByCalculTaxeBetween(BigDecimal minTaux, BigDecimal maxTaux);
    Flux<Taxes> findByMontantBetween(BigDecimal minMontant, BigDecimal maxMontant);
    
    @Query("SELECT COUNT(*) FROM taxes WHERE actif = true")
    Mono<Long> countActiveTaxes();
    
    @Query("SELECT COUNT(*) FROM taxes WHERE type_taxe = :typeTaxe")
    Mono<Long> countByTypeTaxe(String typeTaxe);
}
