package com.example.account.repository;

import com.example.account.model.entity.BonAchat;
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
public interface BonAchatRepository extends JpaRepository<BonAchat, UUID> {
    Page<BonAchat> findAll(Pageable pageable);

    Optional<BonAchat> findByNumeroBon(String numeroBon);

    List<BonAchat> findByIdFournisseur(UUID idFournisseur);

    List<BonAchat> findByIdBonCommande(UUID idBonCommande);

    List<BonAchat> findByStatut(String statut);

    List<BonAchat> findByDevise(String devise);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.dateAchat BETWEEN ?1 AND ?2")
    List<BonAchat> findByDateAchatBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.dateLivraison BETWEEN ?1 AND ?2")
    List<BonAchat> findByDateLivraisonBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.montantTotal BETWEEN ?1 AND ?2")
    List<BonAchat> findByMontantTotalBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.idFournisseur = ?1 AND ba.statut = ?2")
    List<BonAchat> findByFournisseurAndStatut(UUID idFournisseur, String statut);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.numeroBon LIKE %?1%")
    List<BonAchat> findByNumeroBonContaining(String numeroBon);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.nomFournisseur LIKE %?1%")
    List<BonAchat> findByNomFournisseurContaining(String nomFournisseur);

    @Query("SELECT ba FROM BonAchat ba WHERE ba.numeroFactureFournisseur LIKE %?1%")
    List<BonAchat> findByNumeroFactureFournisseurContaining(String numeroFacture);

    @Query("SELECT SUM(ba.montantTotal) FROM BonAchat ba WHERE ba.idFournisseur = ?1")
    BigDecimal sumMontantByFournisseur(UUID idFournisseur);

    @Query("SELECT SUM(ba.montantTotal) FROM BonAchat ba WHERE ba.statut = ?1")
    BigDecimal sumMontantByStatut(String statut);

    @Query("SELECT COUNT(ba) FROM BonAchat ba WHERE ba.statut = ?1")
    Long countByStatut(String statut);

    @Query("SELECT COUNT(ba) FROM BonAchat ba WHERE ba.idFournisseur = ?1")
    Long countByFournisseur(UUID idFournisseur);

    boolean existsByNumeroBon(String numeroBon);
}
