package com.example.account.modules.tiers.repository;

import com.example.account.modules.tiers.model.entity.Fournisseur;
import com.example.account.modules.tiers.model.enums.TypeClient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface FournisseurRepository extends R2dbcRepository<Fournisseur, UUID> {
    Mono<Fournisseur> findByUsername(String username);
    Mono<Fournisseur> findByEmail(String email);
    Mono<Fournisseur> findByCodeFournisseur(String codeFournisseur);
    Flux<Fournisseur> findByActif(Boolean actif);
    Flux<Fournisseur> findByActifTrue();
    Flux<Fournisseur> findByCategorie(String categorie);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByCodeFournisseur(String codeFournisseur);

    @Query("SELECT COUNT(*) FROM fournisseurs WHERE actif = true")
    Mono<Long> countByActifTrue();

    @Query("SELECT COUNT(*) FROM fournisseurs WHERE actif = true")
    Mono<Long> countActiveFournisseurs();

    @Query("SELECT COUNT(*) FROM fournisseurs WHERE type_fournisseur = :typeFournisseur")
    Mono<Long> countFournisseursByType(TypeClient typeFournisseur);

    Flux<Fournisseur> findByTypeFournisseur(TypeClient typeFournisseur);
}
