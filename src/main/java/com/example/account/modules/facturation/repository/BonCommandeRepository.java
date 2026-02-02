package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonCommande;
import com.example.account.modules.facturation.model.enums.StatusBonCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonCommandeRepository extends JpaRepository<BonCommande, UUID> {

    // --- Recherches de base ---
    Optional<BonCommande> findByNumeroCommande(String numeroCommande);
    
    List<BonCommande> findByIdClient(UUID idClient);

    List<BonCommande> findByStatut(StatusBonCommande statut);

    // --- Filtrage par Organisation (Multi-tenancy) ---
    Page<BonCommande> findByOrganizationId(UUID organizationId, Pageable pageable);

    // --- Recherches par Date (Passage à LocalDateTime) ---
    List<BonCommande> findByDateCommandeBetweenAndOrganizationId(LocalDateTime start, LocalDateTime end, UUID orgId);

    // --- Recherches Textuelles ---
    List<BonCommande> findByNumeroCommandeContainingIgnoreCase(String numero);

    List<BonCommande> findByNomClientContainingIgnoreCase(String nomClient);

    // --- Statistiques et Agrégations ---
    @Query("SELECT SUM(bc.montantTTC) FROM BonCommande bc WHERE bc.idClient = ?1 AND bc.organizationId = ?2")
    BigDecimal sumMontantByClient(UUID idClient, UUID organizationId);

    @Query("SELECT COUNT(bc) FROM BonCommande bc WHERE bc.statut = ?1 AND bc.organizationId = ?2")
    Long countByStatutAndOrganizationId(StatusBonCommande statut, UUID organizationId);

    boolean existsByNumeroCommandeAndOrganizationId(String numeroCommande, UUID organizationId);

    // --- Requête Spéciale JSON (Si vous utilisez PostgreSQL JSONB) ---
    // Exemple : Trouver les commandes contenant un produit spécifique dans le champ 'lines'
    @Query(value = "SELECT * FROM bons_commande WHERE lines @> CAST(:jsonQuery AS jsonb)", nativeQuery = true)
    List<BonCommande> findByProductInLines(String jsonQuery);
}