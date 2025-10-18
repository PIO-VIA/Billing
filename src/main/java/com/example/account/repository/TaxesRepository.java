package com.example.account.repository;

import com.example.account.model.entity.Taxes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaxesRepository extends JpaRepository<Taxes, UUID> {
    Page<Taxes> findAll(Pageable pageable);

    Optional<Taxes> findByNomTaxe(String nomTaxe);

    List<Taxes> findByTypeTaxe(String typeTaxe);

    List<Taxes> findByActif(Boolean actif);

    List<Taxes> findByPorteTaxe(String porteTaxe);

    List<Taxes> findByPositionFiscale(String positionFiscale);

    @Query("SELECT t FROM Taxes t WHERE t.actif = true")
    List<Taxes> findAllActiveTaxes();

    @Query("SELECT t FROM Taxes t WHERE t.typeTaxe = ?1 AND t.actif = true")
    List<Taxes> findActiveByTypeTaxe(String typeTaxe);

    @Query("SELECT t FROM Taxes t WHERE t.calculTaxe BETWEEN ?1 AND ?2")
    List<Taxes> findByCalculTaxeBetween(BigDecimal minTaux, BigDecimal maxTaux);

    @Query("SELECT t FROM Taxes t WHERE t.montant BETWEEN ?1 AND ?2")
    List<Taxes> findByMontantBetween(BigDecimal minMontant, BigDecimal maxMontant);

    @Query("SELECT COUNT(t) FROM Taxes t WHERE t.actif = true")
    Long countActiveTaxes();

    @Query("SELECT COUNT(t) FROM Taxes t WHERE t.typeTaxe = ?1")
    Long countByTypeTaxe(String typeTaxe);

    boolean existsByNomTaxe(String nomTaxe);
}
