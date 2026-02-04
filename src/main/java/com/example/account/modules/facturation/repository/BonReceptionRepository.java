package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BondeReception;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BonReceptionRepository extends R2dbcRepository<BondeReception, UUID> {
    Flux<BondeReception> findByOrganizationId(UUID organizationId);
    Mono<BondeReception> findByIdGRNAndOrganizationId(UUID idGRN, UUID organizationId);
    Mono<BondeReception> findByGrnNumberAndOrganizationId(String grnNumber, UUID organizationId);
}
