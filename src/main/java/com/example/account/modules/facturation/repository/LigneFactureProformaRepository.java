package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.LigneFactureProforma;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface LigneFactureProformaRepository extends R2dbcRepository<LigneFactureProforma, UUID> {
    Flux<LigneFactureProforma> findByIdProformaInvoice(UUID idProformaInvoice);
}
