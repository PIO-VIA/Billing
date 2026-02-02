package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonAchatRepository extends JpaRepository<BonAchat, UUID> {

    // Recherche par numéro unique
    Optional<BonAchat> findByNumeroBonAchat(String numeroBonAchat);

    // Recherche par fournisseur (nom mis à jour)
    List<BonAchat> findBySupplierId(UUID supplierId);

    // Vérification d'existence
    boolean existsByNumeroBonAchat(String numeroBonAchat);

    /**
     * Recherche tous les bons d'achat d'une organisation spécifique.
     * Utile car votre entité étend OrganizationScoped.
     */
    List<BonAchat> findByOrganizationId(UUID organizationId);

    /**
     * Recherche par numéro au sein d'une organisation précise pour éviter 
     * les collisions si les numéros ne sont uniques que par client.
     */
    Optional<BonAchat> findByNumeroBonAchatAndOrganizationId(String numeroBonAchat, UUID organizationId);
}