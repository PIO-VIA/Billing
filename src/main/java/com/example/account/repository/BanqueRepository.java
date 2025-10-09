package com.example.account.repository;

import com.example.account.model.entity.Banque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.*;

@Repository
public interface BanqueRepository extends JpaRepository<Banque, UUID> {
    Optional<Banque> findByNumeroCompte(String numeroCompte);
    Page<Banque> findAll(Pageable pageable);

    List<Banque> findByBanque(String banque);

    @Query("SELECT b FROM Banque b WHERE b.banque LIKE %?1%")
    List<Banque> findByBanqueContaining(String banque);

    @Query("SELECT b FROM Banque b WHERE b.numeroCompte LIKE %?1%")
    List<Banque> findByNumeroCompteContaining(String numeroCompte);

    boolean existsByNumeroCompte(String numeroCompte);
}
