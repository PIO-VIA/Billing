package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.LigneBonLivraison;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface LigneBonLivraisonRepository extends R2dbcRepository<LigneBonLivraison, UUID> {
    Flux<LigneBonLivraison> findByIdBonLivraison(UUID idBonLivraison);
}
