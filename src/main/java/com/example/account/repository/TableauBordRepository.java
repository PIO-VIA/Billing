package com.example.account.repository;

import com.example.account.model.entity.TableauBord;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TableauBordRepository extends R2dbcRepository<TableauBord, UUID> {
    Flux<TableauBord> findByProprietaire(UUID proprietaire);
    Flux<TableauBord> findByPartagePublicTrue();
}
