package com.example.account.repository;

import com.example.account.model.entity.Devise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviseRepository extends JpaRepository<Devise, UUID> {
    Page<Devise> findAll(Pageable pageable);

    Optional<Devise> findByNomDevise(String nomDevise);

    Optional<Devise> findBySymbole(String symbole);

    List<Devise> findByActif(Boolean actif);

    @Query("SELECT d FROM Devise d WHERE d.actif = true")
    List<Devise> findAllActiveDevises();

    @Query("SELECT d FROM Devise d WHERE d.nomDevise LIKE %?1%")
    List<Devise> findByNomDeviseContaining(String nomDevise);

    @Query("SELECT COUNT(d) FROM Devise d WHERE d.actif = true")
    Long countActiveDevises();

    boolean existsByNomDevise(String nomDevise);

    boolean existsBySymbole(String symbole);
}
