package com.example.account.repository;

import com.example.account.model.entity.MouvementStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, UUID> {

    Page<MouvementStock> findAll(Pageable pageable);

    Optional<MouvementStock> findByNumeroMouvement(String numeroMouvement);

    List<MouvementStock> findByIdProduit(UUID idProduit);

    List<MouvementStock> findByTypeMouvement(String typeMouvement);

    @Query("SELECT m FROM MouvementStock m WHERE m.dateMouvement BETWEEN ?1 AND ?2")
    List<MouvementStock> findByDateMouvementBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT m FROM MouvementStock m WHERE m.idProduit = ?1 AND m.dateMouvement BETWEEN ?2 AND ?3 ORDER BY m.dateMouvement DESC")
    List<MouvementStock> findHistoriqueProduit(UUID idProduit, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT m FROM MouvementStock m WHERE m.validated = false")
    List<MouvementStock> findMouvementsNonValides();
}
