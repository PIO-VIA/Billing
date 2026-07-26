package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.SellerAuthContext;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface SellerAuthContextRepository extends R2dbcRepository<SellerAuthContext, UUID> {
    Mono<SellerAuthContext> findBySellerId(UUID sellerId);
    Flux<SellerAuthContext> findBySellerIdIn(Collection<UUID> sellerIds);
    Mono<SellerAuthContext> findByOrganizationIdAndPin(UUID organizationId, String pin);
}
