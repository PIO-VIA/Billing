package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.SellerAgencyAssignment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface SellerAgencyAssignmentRepository extends ReactiveCrudRepository<SellerAgencyAssignment, UUID> {
    Mono<SellerAgencyAssignment> findBySellerId(UUID sellerId);
    Flux<SellerAgencyAssignment> findBySellerIdIn(Collection<UUID> sellerIds);
}
