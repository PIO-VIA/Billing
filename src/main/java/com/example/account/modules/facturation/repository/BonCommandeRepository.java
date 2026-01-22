package com.example.account.modules.facturation.repository;

import com.example.account.modules.facturation.model.entity.BonCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonCommandeRepository extends JpaRepository<BonCommande, UUID> {
    Page<BonCommande> findAll(Pageable pageable);

    Optional<BonCommande> findByNumeroCommande(String numeroCommande);

    List<BonCommande> findByIdFournisseur(UUID idFournisseur);

    List<BonCommande> findByStatut(String statut);

    List<BonCommande> findByDevise(String devise);

    @Query("SELECT bc FROM BonCommande bc WHERE bc.dateCommande BETWEEN ?1 AND ?2")
    List<BonCommande> findByDateCommandeBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT bc FROM BonCommande bc WHERE bc.dateLivraisonPrevue BETWEEN ?1 AND ?2")
    List<BonCommande> findByDateLivraisonPrevueBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT bc FROM BonCommande bc WHERE bc.montantTotal BETWEEN ?1 AND ?2")
    List<BonCommande> findByMontantTotalBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT bc FROM BonCommande bc WHERE bc.idFournisseur = ?1 AND bc.statut = ?2")
    List<BonCommande> findByFournisseurAndStatut(UUID idFournisseur, String statut);

    @Query("SELECT bc FROM BonCommande bc WHERE bc.numeroCommande LIKE %?1%")
    List<BonCommande> findByNumeroCommandeContaining(String numeroCommande);

    @Query("SELECT bc FROM BonCommande bc WHERE bc.nomFournisseur LIKE %?1%")
    List<BonCommande> findByNomFournisseurContaining(String nomFournisseur);

    @Query("SELECT SUM(bc.montantTotal) FROM BonCommande bc WHERE bc.idFournisseur = ?1")
    BigDecimal sumMontantByFournisseur(UUID idFournisseur);

    @Query("SELECT SUM(bc.montantTotal) FROM BonCommande bc WHERE bc.statut = ?1")
    BigDecimal sumMontantByStatut(String statut);

    @Query("SELECT COUNT(bc) FROM BonCommande bc WHERE bc.statut = ?1")
    Long countByStatut(String statut);

    @Query("SELECT COUNT(bc) FROM BonCommande bc WHERE bc.idFournisseur = ?1")
    Long countByFournisseur(UUID idFournisseur);

    boolean existsByNumeroCommande(String numeroCommande);
}
