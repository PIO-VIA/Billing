package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.Devis;
import com.example.account.modules.facturation.model.enums.StatutDevis;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DevisRepository extends R2dbcRepository<Devis, UUID> {
    Mono<Devis> findByNumeroDevis(String numeroDevis);
    Flux<Devis> findByStatut(StatutDevis statut);
    Mono<Boolean> existsByNumeroDevis(String numeroDevis);
    
    @Query("SELECT * FROM devis WHERE id_client = :idClient")
    Flux<Devis> findByIdClient(UUID idClient);

    @Query("SELECT * FROM devis WHERE date_expiration < :date AND statut != 'EXPIRE'")
    Flux<Devis> findExpiredDevis(java.time.LocalDate date);

    Flux<Devis> findByDateCreationBetween(java.time.LocalDate start, java.time.LocalDate end);
    
    Flux<Devis> findByOrganizationId(UUID organizationId);
}
