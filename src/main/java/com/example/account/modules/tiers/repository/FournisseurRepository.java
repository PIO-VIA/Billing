package com.example.account.modules.tiers.repository;

import com.example.account.modules.tiers.model.entity.Fournisseur;
import com.example.account.modules.tiers.model.enums.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, UUID> {
    Optional<Fournisseur> findByUsername(String username);
    Optional<Fournisseur> findByEmail(String email);
    Optional<Fournisseur> findByCodeFournisseur(String codeFournisseur);
    List<Fournisseur> findByActif(Boolean actif);
    List<Fournisseur> findByActifTrue();
    List<Fournisseur> findByCategorie(String categorie);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByCodeFournisseur(String codeFournisseur);

    @Query("SELECT COUNT(f) FROM Fournisseur f WHERE f.actif = true")
    Long countByActifTrue();

    @Query("SELECT COUNT(f) FROM Fournisseur f WHERE f.actif = true")
    Long countActiveFournisseurs();

    @Query("SELECT COUNT(f) FROM Fournisseur f WHERE f.typeFournisseur = ?1")
    Long countFournisseursByType(TypeClient typeFournisseur);

    List<Fournisseur> findByTypeFournisseur(TypeClient typeFournisseur);
}
