package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.FactureProforma;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface FactureProformaRepository extends R2dbcRepository<FactureProforma, UUID> {
    Mono<FactureProforma> findByNumeroProformaInvoice(String numeroProformaInvoice);
    Flux<FactureProforma> findByIdClient(UUID idClient);
    Mono<Boolean> existsByNumeroProformaInvoice(String numeroProformaInvoice);
}
