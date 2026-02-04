package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonCommande;
import com.example.account.modules.facturation.model.enums.StatusBonCommande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface BonCommandeRepository extends R2dbcRepository<BonCommande, UUID> {

    // --- Recherches de base ---
    Mono<BonCommande> findByNumeroCommande(String numeroCommande);
    
    Flux<BonCommande> findByIdClient(UUID idClient);

    Flux<BonCommande> findByStatut(StatusBonCommande statut);

    // --- Filtrage par Organisation (Multi-tenancy) ---
    Flux<BonCommande> findByOrganizationId(UUID organizationId, Pageable pageable);

    // --- Recherches par Date ---
    Flux<BonCommande> findByDateCommandeBetweenAndOrganizationId(LocalDateTime start, LocalDateTime end, UUID orgId);

    // --- Recherches Textuelles ---
    Flux<BonCommande> findByNumeroCommandeContainingIgnoreCase(String numero);

    Flux<BonCommande> findByNomClientContainingIgnoreCase(String nomClient);

    // --- Statistiques et Agrégations ---
    @Query("SELECT SUM(bc.montant_ttc) FROM bons_commande bc WHERE bc.id_client = :idClient AND bc.organization_id = :organizationId")
    Mono<BigDecimal> sumMontantByClient(UUID idClient, UUID organizationId);

    @Query("SELECT COUNT(*) FROM bons_commande bc WHERE bc.statut = :statut AND bc.organization_id = :organizationId")
    Mono<Long> countByStatutAndOrganizationId(StatusBonCommande statut, UUID organizationId);

    Mono<Boolean> existsByNumeroCommandeAndOrganizationId(String numeroCommande, UUID organizationId);

    // --- Requête Spéciale JSON (PostgreSQL JSONB) ---
    @Query(value = "SELECT * FROM bons_commande WHERE lines @> CAST(:jsonQuery AS jsonb)")
    Flux<BonCommande> findByProductInLines(String jsonQuery);
}