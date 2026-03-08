package com.example.account.modules.facturation.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.account.modules.facturation.service.ExternalServices.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.List;


@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    @Query("SELECT * FROM products WHERE id = :id")
    Mono<Product> findByUuid(UUID id);
    
    Flux<Product>  findByOrganizationId(UUID organizationId);
}