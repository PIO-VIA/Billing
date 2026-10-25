package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.ProductSaleSizeConfig;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

public interface ProductSaleSizeConfigRepository extends ReactiveCrudRepository<ProductSaleSizeConfig, UUID> {
    Mono<ProductSaleSizeConfig> findByProductId(UUID productId);
    Flux<ProductSaleSizeConfig> findByProductIdIn(Collection<UUID> productIds);
}
