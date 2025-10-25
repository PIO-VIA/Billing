package com.example.account.repository;

import com.example.account.model.entity.BonLivraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonLivraisonRepository extends JpaRepository<BonLivraison, UUID> {

    Page<BonLivraison> findAll(Pageable pageable);

    Optional<BonLivraison> findByNumeroBonLivraison(String numeroBonLivraison);

    List<BonLivraison> findByIdClient(UUID idClient);

    List<BonLivraison> findByIdFacture(UUID idFacture);

    List<BonLivraison> findByStatut(String statut);

    @Query("SELECT bl FROM BonLivraison bl WHERE bl.dateLivraison BETWEEN ?1 AND ?2")
    List<BonLivraison> findByDateLivraisonBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT bl FROM BonLivraison bl WHERE bl.livraisonEffectuee = false AND bl.dateLivraison < ?1")
    List<BonLivraison> findLivraisonsEnRetard(LocalDate dateReference);

    boolean existsByNumeroBonLivraison(String numeroBonLivraison);
}
