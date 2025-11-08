package com.example.account.repository;

import com.example.account.model.entity.Produit
;
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
public interface ProduitRepository extends JpaRepository<Produit, UUID> {
    Page<Produit> findAll(Pageable pageable);

    Optional<Produit> findByReference(String reference);

    Optional<Produit> findByCodeBarre(String codeBarre);

    List<Produit> findByNomProduit(String nomProduit);

    List<Produit> findByTypeProduit(String typeProduit);

    List<Produit> findByCategorie(String categorie);

    List<Produit> findByActive(Boolean active);

    @Query("SELECT p FROM Produit p WHERE p.active = true")
    List<Produit> findAllActiveProducts();

    @Query("SELECT p FROM Produit p WHERE p.nomProduit LIKE %?1%")
    List<Produit> findByNomProduitContaining(String nomProduit);

    @Query("SELECT p FROM Produit p WHERE p.prixVente BETWEEN ?1 AND ?2")
    List<Produit> findByPrixVenteBetween(BigDecimal minPrice, BigDecimal maxPrice);
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.categorie = ?1")
    Long countByCategorie(String categorie);

    @Query("SELECT COUNT(p) FROM Produit p WHERE p.typeProduit = ?1")
    Long countByTypeProduit(String typeProduit);

    @Query("SELECT COUNT(p) FROM Produit p WHERE p.active = ?1")
    Long countByActive(Boolean active);

    boolean existsByReference(String reference);
}
