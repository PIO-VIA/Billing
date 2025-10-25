package com.example.account.repository;

import com.example.account.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByIdProduit(UUID idProduit);

    Optional<Stock> findByIdProduitAndEmplacement(UUID idProduit, String emplacement);

    List<Stock> findByEmplacement(String emplacement);

    List<Stock> findByStatut(String statut);

    @Query("SELECT s FROM Stock s WHERE s.quantite < s.stockMinimum AND s.statut = 'ACTIF'")
    List<Stock> findStocksSousMinimum();

    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible <= :seuil AND s.statut = 'ACTIF'")
    List<Stock> findStocksAvecSeuilCritique(BigDecimal seuil);

    @Query("SELECT SUM(s.valeurStock) FROM Stock s WHERE s.statut = 'ACTIF'")
    BigDecimal calculerValeurTotaleStock();

    @Query("SELECT s FROM Stock s WHERE s.zone = ?1")
    List<Stock> findByZone(String zone);
}
